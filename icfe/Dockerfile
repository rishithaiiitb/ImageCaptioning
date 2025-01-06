FROM node:alpine

WORKDIR /frontend

COPY . /frontend

RUN npm install --legacy-peer-deps

RUN npm run build

CMD ["npm","start"]
