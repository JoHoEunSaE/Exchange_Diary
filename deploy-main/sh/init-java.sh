# !/bin/sh

mkdir -p BOOT-INF/classes

cp -R /resources/* BOOT-INF/classes

jar uf diary.jar BOOT-INF/classes

java -jar -Dloader.path="/resources/" -Dspring.profiles.active=prod ${JAVA_OPTS} diary.jar --spring.config.location=file:/resources/
