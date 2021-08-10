FROM openjdk:16.0-jre

WORKDIR /home/container
ADD build/libs/FishingGuard-1.0-all.jar .
CMD java -cp FishingGuard-1.0-all.jar ru.ruscalworld.fishingguard.FishingGuard