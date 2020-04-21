FROM openjdk:8-jre-slim

USER root

WORKDIR server

COPY jars /jars

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl git build-essential ruby ruby-dev rubygems && \
    echo "gem: --no-rdoc --no-ri" > ~/.gemrc && \
    gem install bundler

ARG CI_JOB_TOKEN
RUN git clone -b master --depth 1 https://gitlab-ci-token:${CI_JOB_TOKEN}@gitlab.com/WalrusNetwork/infrastructure/mc-bridge.git
RUN mv mc-bridge/* .

RUN bundle install --without test worker

RUN apt-get remove -y build-essential ruby-dev rubygems && \
    apt-get -y autoremove

ARG DATA={}
ENV DATA=$DATA