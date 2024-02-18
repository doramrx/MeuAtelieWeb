FROM node:20.11.1-alpine

WORKDIR /frontend

COPY . .

RUN npm install
RUN npm install -g @angular/cli

ENTRYPOINT ["ng", "serve", "--host", "0.0.0.0", "--poll", "500", "--disable-host-check"]
