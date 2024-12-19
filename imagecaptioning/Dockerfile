# Base image
FROM openjdk:17

# Expose the desired port
EXPOSE 8020

# Set environment variables for New Relic
ENV NEW_RELIC_LICENSE_KEY=23ce2b29dab3435fedbd14dd3cd444feFFFFNRAL
ENV NEW_RELIC_APP_NAME=image-captioning-backend

# Copy New Relic files into the container
COPY ./newrelic /newrelic  

# Copy the application JAR file
ADD ./target/imagecaptioning-0.0.1-SNAPSHOT.jar ./imagecaptioning.jar

# Set the working directory in the container
WORKDIR ./

# Run the application with New Relic agent
CMD ["java", "-javaagent:/newrelic/newrelic.jar", "-jar", "imagecaptioning.jar"]

