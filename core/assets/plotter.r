#!/usr/bin/Rscript

args = commandArgs(TRUE)
data = read.csv(args[1],sep = ',', dec = '.',  header = TRUE, stringsAsFactors = FALSE)

frame = data.frame(data)
frame$Fitness <- as.numeric(frame$Fitness)
frame$Generation <- as.numeric(frame$Generation)

png('result.png', width = 1024)
boxplot(Fitness ~ Generation, frame)
dev.off()
