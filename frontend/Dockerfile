FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
COPY src ./src
RUN npm ci
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
