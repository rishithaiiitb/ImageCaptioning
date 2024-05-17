package com.spe.imagecaptioning.Service;

import com.spe.imagecaptioning.DAO.ImagesDAO;
import com.spe.imagecaptioning.DAO.UserDAO;
import com.spe.imagecaptioning.DTO.LoginDTO;
import com.spe.imagecaptioning.Entity.Images;
import com.spe.imagecaptioning.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ImagesDAO imagesDAO;

    private final RestTemplate icrestTemplate;

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Value("${ic_service.base.url}")
    private String icBaseURL;

    public UserServiceImpl(RestTemplateBuilder builder) {
        this.icrestTemplate = builder.build();
    }

    @Override
    public Boolean verifyUser(LoginDTO loginDTO) {
        User loginUser = userDAO.findByEmail(loginDTO.getEmail());
        if (loginUser == null) {
            logger.error("No user found with given email");
            return false;
        } else {
            logger.info("User found");
            if (loginUser.isPasswordMatch(loginDTO.getPassword())) {
                logger.info("Logged in successfully");
                return true;
            } else {
                logger.error("Wrong password");
                return false;
            }
        }
    }

    @Override
    public User registerUser(User newUser) {
        if (Objects.equals(newUser.getEmail(), "")) {
            logger.error("User Details cannot be null");
            return null;
        } else {
            logger.info("New user created");
            return userDAO.save(newUser);
        }
    }

    @Override
    public List<Images> pastImages(String email) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            logger.warn("No user found with given email");
            return null;
        }
        List<Images> images = imagesDAO.findAllByUserId(user.getUserId());
        if (images == null || images.isEmpty()) {
            logger.warn("No images found for user");
            return null;
        } else {
            logger.info("Images found for user");
            return images;
        }
    }

    @Override
    public String generateCaption(Images image, String email) {
        String djangoUrl = icBaseURL + "/predict/";
        String imageData = image.getImage();

        User user = userDAO.findByEmail(email);
        if (user == null) {
            logger.warn("No user found with given email");
            return null;
        }

        if (imageData == null || imageData.isEmpty()) {
            logger.warn("No image received");
            return null;
        } else {
            logger.info("Received Image, Generating Caption");
            try {
                String response = icrestTemplate.postForObject(djangoUrl, "{\"image\": \"" + imageData + "\"}", String.class);
                JSONObject jsonResponse = new JSONObject(response);
                String generatedCaption = jsonResponse.getString("caption");

                if (generatedCaption != null) {
                    logger.info("Generated Caption for image successfully");
                    // Save the image and its generated caption to the database
                    Images newImage = new Images();
                    newImage.setCreatedAt(LocalDate.now());
                    newImage.setImage(imageData);
                    newImage.setCaption(generatedCaption);
                    newImage.setUser(user);
                    imagesDAO.save(newImage);
                    return generatedCaption;
                } else {
                    logger.error("Generating Caption for image failed");
                    return null;
                }
            } catch (HttpClientErrorException e) {
                logger.error("Error while generating caption: {}", e.getMessage());
                return null;
            }
        }
    }
}
