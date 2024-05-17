FROM node:alpine

WORKDIR /frontend

COPY . /frontend

RUN npm install --legacy-peer-deps

CMD ["npm","start"]