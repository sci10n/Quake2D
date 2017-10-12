#!/usr/bin/Rscript

data = read.csv("statistics_18-32-55",sep = ',', dec = '.',  header = TRUE, stringsAsFactors = FALSE)

frame = data.frame(data)
frame$Fitness <- as.numeric(frame$Fitness)
frame$Generation <- as.numeric(frame$Generation)

#png('result.png', width = 1024)
boxplot(Fitness ~ Generation, frame)
#dev.off()

dev.new()
plot(aggregate(frame$Victory, by=list(frame$Generation), FUN=sum), ylab="Victories", xlab = "Generations"
    )