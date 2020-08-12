FROM 91docker/centos-7-node-12-15-0:0.2

RUN yum update -y

#RUN source ~/.bashrc
ENV PATH /root/.nvm/versions/node/v12.15.0/bin:${PATH}

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.262.b10-0.el7_8.x86_64

RUN mkdir -p $HOME/91cm

COPY . $HOME/91cm

WORKDIR $HOME/91cm/frontend

RUN npm install

WORKDIR $HOME/91cm

CMD ["./gradlew","bootRun"]

EXPOSE 9191