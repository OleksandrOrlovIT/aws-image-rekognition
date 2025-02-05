# Build the React app
FROM node:22 AS build

WORKDIR /app

COPY . .

RUN npm install
RUN npm run build

# Serve the app in production
FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

# Expose React app port
EXPOSE 80
