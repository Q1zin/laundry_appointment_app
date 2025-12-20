FROM node:22.12.0-bookworm-slim AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci --include=optional --no-audit --no-fund

COPY . .
# Work around npm optionalDependencies + Rollup native package issues in containers
RUN node -e 'const {execSync}=require("node:child_process"); const v=require("./node_modules/rollup/package.json").version; const arch=(process.arch==="x64"?"x64":(process.arch==="arm64"?"arm64":process.arch)); const pkg="@rollup/rollup-linux-"+arch+"-gnu@"+v; try{require("rollup");}catch(e){console.log("Installing", pkg); execSync("npm i -D "+pkg+" --no-audit --no-fund", {stdio:"inherit"}); }'

RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
