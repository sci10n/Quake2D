
data = read.csv("statistics_Thu Oct 12 10:44:36 CEST 2017",sep = ',', dec = '.',  header = TRUE, stringsAsFactors = FALSE)

frame = data.frame(data)
frame$Fitness <- as.numeric(frame$Fitness)
frame$Generation <- as.numeric(frame$Generation)

boxplot(Fitness ~ Generation, frame)