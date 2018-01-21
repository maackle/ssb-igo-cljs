# specify the node base image with your desired version node:<version>
# NB: Must match lumo node version!!
FROM node:8.5
RUN apt update
RUN apt install -y vim
RUN npm config set user 0
RUN npm config set unsafe-perm true
RUN npm --loglevel=warn install -g scuttlebot@10.4.6
RUN npm --loglevel=warn install -g lumo-cljs@1.8.0-beta

# for testnet
ENV SBOT_SHS GVZDyNf1TrZuGv3W5Dpef0vaITW1UqOUO3aWLNBp+7A=
ENV SBOT_SIGN gym3eJKBjm0E0OIjuh3O1VX8+lLVSGV2p5UzrMStHTs=

WORKDIR /code

ADD ./dev/bashrc /root/.bashrc

ADD ./package.json .

RUN npm install

EXPOSE 8008

CMD dev/testnet
