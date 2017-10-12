data = read.csv2("statistics_1692238764",header = TRUE, sep = ',', stringsAsFactors = FALSE)

frame = data.frame(data)
plot(data)


abline(lm(Fitness ~ Generation, frame))