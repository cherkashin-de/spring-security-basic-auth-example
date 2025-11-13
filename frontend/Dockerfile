# Этап 1: билд React
FROM node:18 AS build
WORKDIR /app

# Копируем package.json и зависимости (если появятся)
COPY package*.json ./
RUN npm install

# Копируем исходники
COPY . .

# Собираем bundle и кладём в build
ENV REACT_APP_API_URL=
RUN npm run build && cp index.html build/index.html

# Этап 2: nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
