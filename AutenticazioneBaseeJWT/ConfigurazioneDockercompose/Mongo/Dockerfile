FROM ubuntu:18.04

MAINTAINER nicola.larocca@gmail.com

RUN apt-get update && apt-get install -y mongodb && apt-get install nano

# Create the MongoDB data directory
RUN mkdir -p /data/db

# Expose port #37017 from the container to the host
EXPOSE 37017

#Start MongoDb
CMD ["mongod", "-f", "/etc/mongodb.conf"]



