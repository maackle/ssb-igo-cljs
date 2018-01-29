# specify the node base image with your desired version node:<version>
# NB: Must match lumo node version!!
FROM openjdk
RUN apt update
RUN apt install -y vim less

RUN curl -sL https://deb.nodesource.com/setup_8.x | bash
RUN apt-get install -y nodejs build-essential

RUN npm config set user 0
RUN npm config set unsafe-perm true
RUN npm --loglevel=warn install -g scuttlebot@10.4.6
RUN npm --loglevel=warn install -g lumo-cljs@1.8.0-beta
RUN npm --loglevel=warn install -g calvin-cljs@0.3.0

RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > /bin/lein
RUN chmod a+x /bin/lein
RUN /bin/lein

# for testnet
ENV SBOT_SHS GVZDyNf1TrZuGv3W5Dpef0vaITW1UqOUO3aWLNBp+7A=
ENV SBOT_SIGN gym3eJKBjm0E0OIjuh3O1VX8+lLVSGV2p5UzrMStHTs=

ENV SBOT_SHS_TEST GVZDyNf1TrZuGv3W5Dpef0vaITW1UqOUO3aWLNBp-7A=

WORKDIR /code

ADD ./dev/bashrc /root/.bashrc

ADD ./package.json .
RUN npm install

ADD ./project.clj .

RUN lein deps

# TODO: move up top :(
RUN apt install -y tmux rlwrap

RUN echo "alias ll='ls -a'" >> ~/.bashrc && source ~/.bashrc

EXPOSE 8008

CMD dev/testnet
