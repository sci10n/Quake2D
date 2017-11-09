#!/usr/bin/Rscript

args = commandArgs(TRUE)
data = read.csv(args[1],sep = ',', dec = '.',  header = TRUE, stringsAsFactors = FALSE)

frame = data.frame(data)
frame$Fitness <- as.numeric(frame$Fitness)
frame$Generation <- as.numeric(frame$Generation)


png('result.png', width = 1024)
boxplot(Fitness ~ Generation, frame)
dev.off()

dev.new()
png('result-victories-scatter.png', width = 1024)
plot(frame$Generation, frame$Fitness, col=frame$Victory+2, ylab="Victories", xlab = "Generations")
dev.off()

dev.new()
png('result-victories.png', width = 1024)
plot(aggregate(frame$Victory, by=list(frame$Generation), FUN=sum), ylab="Victories", xlab = "Generations")
dev.off()