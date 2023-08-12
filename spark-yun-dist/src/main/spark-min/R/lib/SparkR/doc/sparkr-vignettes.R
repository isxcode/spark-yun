## ----dynamic-chunk-options, include=FALSE-------------------------------------
# In GitHub lint job, we don't have full JVM build
# SparkR vignette fails to evaluate
GITHUB_ACTIONS <- tolower(Sys.getenv("GITHUB_ACTIONS")) == "true"
EVAL_CHUNK <- !GITHUB_ACTIONS

## ----setup, include=FALSE, eval=EVAL_CHUNK------------------------------------
library(knitr)
opts_hooks$set(eval = function(options) {
  # override eval to FALSE only on windows
  if (.Platform$OS.type == "windows") {
    options$eval = FALSE
  }
  options
})
r_tmp_dir <- tempdir()
tmp_arg <- paste0("-Djava.io.tmpdir=", r_tmp_dir)
sparkSessionConfig <- list(spark.driver.extraJavaOptions = tmp_arg,
                           spark.executor.extraJavaOptions = tmp_arg)
old_java_opt <- Sys.getenv("_JAVA_OPTIONS")
Sys.setenv("_JAVA_OPTIONS" = paste("-XX:-UsePerfData", old_java_opt, sep = " "))

## ---- message=FALSE, eval=EVAL_CHUNK------------------------------------------
library(SparkR)

## ---- include=FALSE, eval=EVAL_CHUNK------------------------------------------
# disable eval if java version not supported
override_eval <- tryCatch(!is.numeric(SparkR:::checkJavaVersion()),
          error = function(e) { TRUE },
          warning = function(e) { TRUE })

if (override_eval) {
  opts_hooks$set(eval = function(options) {
    options$eval = FALSE
    options
  })
}

## ---- include=FALSE, eval=EVAL_CHUNK------------------------------------------
install.spark()
sparkR.session(master = "local[1]", sparkConfig = sparkSessionConfig, enableHiveSupport = FALSE)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
sparkR.session()

## ---- eval=EVAL_CHUNK---------------------------------------------------------
cars <- cbind(model = rownames(mtcars), mtcars)
carsDF <- createDataFrame(cars)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
head(carsDF)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsSubDF <- select(carsDF, "model", "mpg", "hp")
carsSubDF <- filter(carsSubDF, carsSubDF$hp >= 200)
head(carsSubDF)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsGPDF <- summarize(groupBy(carsDF, carsDF$gear), count = n(carsDF$gear))
head(carsGPDF)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsGP <- collect(carsGPDF)
class(carsGP)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
model <- spark.glm(carsDF, mpg ~ wt + cyl)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
summary(model)

## ---- eval=FALSE--------------------------------------------------------------
#  write.ml(model, path = "/HOME/tmp/mlModel/glmModel")

## ---- eval=FALSE--------------------------------------------------------------
#  sparkR.session.stop()

## ---- eval=FALSE--------------------------------------------------------------
#  install.spark()

## ---- eval=FALSE--------------------------------------------------------------
#  sparkR.session(sparkHome = "/HOME/spark")

## ---- eval=FALSE--------------------------------------------------------------
#  spark_warehouse_path <- file.path(path.expand('~'), "spark-warehouse")
#  sparkR.session(spark.sql.warehouse.dir = spark_warehouse_path)

## ---- echo=FALSE, tidy = TRUE, eval=EVAL_CHUNK--------------------------------
paste("Spark", packageVersion("SparkR"))

## ---- eval=FALSE--------------------------------------------------------------
#  sparkR.session(master = "spark://local:7077")

## ---- eval=FALSE--------------------------------------------------------------
#  sparkR.session(master = "yarn")

## ---- eval=EVAL_CHUNK---------------------------------------------------------
df <- as.DataFrame(faithful)
head(df)

## ---- eval=FALSE--------------------------------------------------------------
#  sparkR.session(sparkPackages = "com.databricks:spark-avro_2.12:3.0.0")

## ---- eval=FALSE--------------------------------------------------------------
#  df <- read.df(csvPath, "csv", header = "true", inferSchema = "true", na.strings = "NA")

## ---- eval=EVAL_CHUNK---------------------------------------------------------
filePath <- paste0(sparkR.conf("spark.home"),
                         "/examples/src/main/resources/people.json")
readLines(filePath, n = 2L)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
people <- read.df(filePath, "json")
count(people)
head(people)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
printSchema(people)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
people <- read.json(paste0(Sys.getenv("SPARK_HOME"),
                           c("/examples/src/main/resources/people.json",
                             "/examples/src/main/resources/people.json")))
count(people)

## ---- eval=FALSE--------------------------------------------------------------
#  write.df(people, path = "people.parquet", source = "parquet", mode = "overwrite")

## ---- eval=FALSE--------------------------------------------------------------
#  sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING)")
#  
#  txtPath <- paste0(sparkR.conf("spark.home"), "/examples/src/main/resources/kv1.txt")
#  sqlCMD <- sprintf("LOAD DATA LOCAL INPATH '%s' INTO TABLE src", txtPath)
#  sql(sqlCMD)
#  
#  results <- sql("FROM src SELECT key, value")
#  
#  # results is now a SparkDataFrame
#  head(results)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsDF

## ---- eval=EVAL_CHUNK---------------------------------------------------------
printSchema(carsDF)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
head(select(carsDF, "mpg"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
head(filter(carsDF, carsDF$mpg < 20))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
numCyl <- summarize(groupBy(carsDF, carsDF$cyl), count = n(carsDF$cyl))
head(numCyl)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
mean(cube(carsDF, "cyl", "gear", "am"), "mpg")

## ---- eval=EVAL_CHUNK---------------------------------------------------------
mean(rollup(carsDF, "cyl", "gear", "am"), "mpg")

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsDF_km <- carsDF
carsDF_km$kmpg <- carsDF_km$mpg * 1.61
head(select(carsDF_km, "model", "mpg", "kmpg"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsSubDF <- select(carsDF, "model", "mpg", "cyl")
ws <- orderBy(windowPartitionBy("cyl"), "mpg")
carsRank <- withColumn(carsSubDF, "rank", over(rank(), ws))
head(carsRank, n = 20L)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
carsSubDF <- select(carsDF, "model", "mpg")
schema <- "model STRING, mpg DOUBLE, kmpg DOUBLE"
out <- dapply(carsSubDF, function(x) { x <- cbind(x, x$mpg * 1.61) }, schema)
head(collect(out))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
out <- dapplyCollect(
         carsSubDF,
         function(x) {
           x <- cbind(x, "kmpg" = x$mpg * 1.61)
         })
head(out, 3)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
schema <- structType(structField("cyl", "double"), structField("max_mpg", "double"))
result <- gapply(
    carsDF,
    "cyl",
    function(key, x) {
        y <- data.frame(key, max(x$mpg))
    },
    schema)
head(arrange(result, "max_mpg", decreasing = TRUE))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
result <- gapplyCollect(
    carsDF,
    "cyl",
    function(key, x) {
         y <- data.frame(key, max(x$mpg))
        colnames(y) <- c("cyl", "max_mpg")
        y
    })
head(result[order(result$max_mpg, decreasing = TRUE), ])

## ---- eval=EVAL_CHUNK---------------------------------------------------------
costs <- exp(seq(from = log(1), to = log(1000), length.out = 5))
train <- function(cost) {
  stopifnot(requireNamespace("e1071", quietly = TRUE))
  model <- e1071::svm(Species ~ ., data = iris, cost = cost)
  summary(model)
}

## ---- eval=EVAL_CHUNK---------------------------------------------------------
model.summaries <- spark.lapply(costs, train)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
class(model.summaries)

## ---- include=FALSE, eval=EVAL_CHUNK------------------------------------------
ops <- options()
options(max.print=40)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
print(model.summaries[[2]])

## ---- include=FALSE, eval=EVAL_CHUNK------------------------------------------
options(ops)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
people <- read.df(paste0(sparkR.conf("spark.home"),
                         "/examples/src/main/resources/people.json"), "json")

## ---- eval=EVAL_CHUNK---------------------------------------------------------
createOrReplaceTempView(people, "people")

## ---- eval=EVAL_CHUNK---------------------------------------------------------
teenagers <- sql("SELECT name FROM people WHERE age >= 13 AND age <= 19")
head(teenagers)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
splitDF_list <- randomSplit(carsDF, c(0.7, 0.3), seed = 0)
carsDF_train <- splitDF_list[[1]]
carsDF_test <- splitDF_list[[2]]

## ---- eval=EVAL_CHUNK---------------------------------------------------------
count(carsDF_train)
head(carsDF_train)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
count(carsDF_test)
head(carsDF_test)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
# load training data and create a DataFrame
t <- as.data.frame(Titanic)
training <- createDataFrame(t)
# fit a Linear SVM classifier model
model <- spark.svmLinear(training,  Survived ~ ., regParam = 0.01, maxIter = 10)
summary(model)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
prediction <- predict(model, training)
head(select(prediction, "Class", "Sex", "Age", "Freq", "Survived", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
training <- createDataFrame(t)
model <- spark.logit(training, Survived ~ ., regParam = 0.04741301)
summary(model)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
fitted <- predict(model, training)
head(select(fitted, "Class", "Sex", "Age", "Freq", "Survived", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
training <- createDataFrame(t)
# Note in this case, Spark infers it is multinomial logistic regression, so family = "multinomial" is optional.
model <- spark.logit(training, Class ~ ., regParam = 0.07815179)
summary(model)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
training <- createDataFrame(t)
# fit a Multilayer Perceptron Classification Model
model <- spark.mlp(training, Survived ~ Age + Sex, blockSize = 128, layers = c(2, 2), solver = "l-bfgs", maxIter = 100, tol = 0.5, stepSize = 1, seed = 1, initialWeights = c( 0, 0, 5, 5, 9, 9))

## ---- include=FALSE, eval=EVAL_CHUNK------------------------------------------
ops <- options()
options(max.print=5)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
# check the summary of the fitted model
summary(model)

## ---- include=FALSE, eval=EVAL_CHUNK------------------------------------------
options(ops)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
# make predictions use the fitted model
predictions <- predict(model, training)
head(select(predictions, predictions$prediction))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
titanic <- as.data.frame(Titanic)
titanicDF <- createDataFrame(titanic[titanic$Freq > 0, -5])
naiveBayesModel <- spark.naiveBayes(titanicDF, Survived ~ Class + Sex + Age)
summary(naiveBayesModel)
naiveBayesPrediction <- predict(naiveBayesModel, titanicDF)
head(select(naiveBayesPrediction, "Class", "Sex", "Age", "Survived", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
training <- createDataFrame(t)

model <- spark.fmClassifier(training, Survived ~ Age + Sex)
summary(model)

predictions <- predict(model, training)
head(select(predictions, predictions$prediction))

## ---- warning=FALSE, eval=EVAL_CHUNK------------------------------------------
library(survival)
ovarianDF <- createDataFrame(ovarian)
aftModel <- spark.survreg(ovarianDF, Surv(futime, fustat) ~ ecog_ps + rx)
summary(aftModel)
aftPredictions <- predict(aftModel, ovarianDF)
head(aftPredictions)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
gaussianGLM <- spark.glm(carsDF, mpg ~ wt + hp)
summary(gaussianGLM)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
gaussianFitted <- predict(gaussianGLM, carsDF)
head(select(gaussianFitted, "model", "prediction", "mpg", "wt", "hp"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
tweedieGLM1 <- spark.glm(carsDF, mpg ~ wt + hp, family = "tweedie", var.power = 0.0)
summary(tweedieGLM1)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
tweedieGLM2 <- spark.glm(carsDF, mpg ~ wt + hp, family = "tweedie",
                         var.power = 1.2, link.power = 0.0)
summary(tweedieGLM2)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
y <- c(3.0, 6.0, 8.0, 5.0, 7.0)
x <- c(1.0, 2.0, 3.5, 3.0, 4.0)
w <- rep(1.0, 5)
data <- data.frame(y = y, x = x, w = w)
df <- createDataFrame(data)
isoregModel <- spark.isoreg(df, y ~ x, weightCol = "w")
isoregFitted <- predict(isoregModel, df)
head(select(isoregFitted, "x", "y", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
newDF <- createDataFrame(data.frame(x = c(1.5, 3.2)))
head(predict(isoregModel, newDF))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
model <- spark.lm(carsDF, mpg ~ wt + hp)

summary(model)
predictions <- predict(model, carsDF)
head(select(predictions, predictions$prediction))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
model <- spark.fmRegressor(carsDF, mpg ~ wt + hp)
summary(model)
predictions <- predict(model, carsDF)
head(select(predictions, predictions$prediction))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
df <- createDataFrame(t)
dtModel <- spark.decisionTree(df, Survived ~ ., type = "classification", maxDepth = 2)
summary(dtModel)
predictions <- predict(dtModel, df)
head(select(predictions, "Class", "Sex", "Age", "Freq", "Survived", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
df <- createDataFrame(t)
gbtModel <- spark.gbt(df, Survived ~ ., type = "classification", maxDepth = 2, maxIter = 2)
summary(gbtModel)
predictions <- predict(gbtModel, df)
head(select(predictions, "Class", "Sex", "Age", "Freq", "Survived", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
df <- createDataFrame(t)
rfModel <- spark.randomForest(df, Survived ~ ., type = "classification", maxDepth = 2, numTrees = 2)
summary(rfModel)
predictions <- predict(rfModel, df)
head(select(predictions, "Class", "Sex", "Age", "Freq", "Survived", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
training <- createDataFrame(t)
model <- spark.bisectingKmeans(training, Class ~ Survived, k = 4)
summary(model)
fitted <- predict(model, training)
head(select(fitted, "Class", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
X1 <- data.frame(V1 = rnorm(4), V2 = rnorm(4))
X2 <- data.frame(V1 = rnorm(6, 3), V2 = rnorm(6, 4))
data <- rbind(X1, X2)
df <- createDataFrame(data)
gmmModel <- spark.gaussianMixture(df, ~ V1 + V2, k = 2)
summary(gmmModel)
gmmFitted <- predict(gmmModel, df)
head(select(gmmFitted, "V1", "V2", "prediction"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
kmeansModel <- spark.kmeans(carsDF, ~ mpg + hp + wt, k = 3)
summary(kmeansModel)
kmeansPredictions <- predict(kmeansModel, carsDF)
head(select(kmeansPredictions, "model", "mpg", "hp", "wt", "prediction"), n = 20L)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
corpus <- data.frame(features = c(
  "1 2 6 0 2 3 1 1 0 0 3",
  "1 3 0 1 3 0 0 2 0 0 1",
  "1 4 1 0 0 4 9 0 1 2 0",
  "2 1 0 3 0 0 5 0 2 3 9",
  "3 1 1 9 3 0 2 0 0 1 3",
  "4 2 0 3 4 5 1 1 1 4 0",
  "2 1 0 3 0 0 5 0 2 2 9",
  "1 1 1 9 2 1 2 0 0 1 3",
  "4 4 0 3 4 2 1 3 0 0 0",
  "2 8 2 0 3 0 2 0 2 7 2",
  "1 1 1 9 0 2 2 0 0 3 3",
  "4 1 0 0 4 5 1 3 0 1 0"))
corpusDF <- createDataFrame(corpus)
model <- spark.lda(data = corpusDF, k = 5, optimizer = "em")
summary(model)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
posterior <- spark.posterior(model, corpusDF)
head(posterior)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
perplexity <- spark.perplexity(model, corpusDF)
perplexity

## ---- eval=FALSE--------------------------------------------------------------
#  ratings <- list(list(0, 0, 4.0), list(0, 1, 2.0), list(1, 1, 3.0), list(1, 2, 4.0),
#                  list(2, 1, 1.0), list(2, 2, 5.0))
#  df <- createDataFrame(ratings, c("user", "item", "rating"))
#  model <- spark.als(df, "rating", "user", "item", rank = 10, reg = 0.1, nonnegative = TRUE)

## ---- eval=FALSE--------------------------------------------------------------
#  stats <- summary(model)
#  userFactors <- stats$userFactors
#  itemFactors <- stats$itemFactors
#  head(userFactors)
#  head(itemFactors)

## ---- eval=FALSE--------------------------------------------------------------
#  predicted <- predict(model, df)
#  head(predicted)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
df <- createDataFrame(list(list(0L, 1L, 1.0), list(0L, 2L, 1.0),
                           list(1L, 2L, 1.0), list(3L, 4L, 1.0),
                           list(4L, 0L, 0.1)),
                      schema = c("src", "dst", "weight"))
head(spark.assignClusters(df, initMode = "degree", weightCol = "weight"))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
df <- selectExpr(createDataFrame(data.frame(rawItems = c(
  "T,R,U", "T,S", "V,R", "R,U,T,V", "R,S", "V,S,U", "U,R", "S,T", "V,R", "V,U,S",
  "T,V,U", "R,V", "T,S", "T,S", "S,T", "S,U", "T,R", "V,R", "S,V", "T,S,U"
))), "split(rawItems, ',') AS items")

fpm <- spark.fpGrowth(df, minSupport = 0.2, minConfidence = 0.5)

## ---- eval=EVAL_CHUNK---------------------------------------------------------
head(spark.freqItemsets(fpm))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
head(spark.associationRules(fpm))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
head(predict(fpm, df))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
df <- createDataFrame(list(list(list(list(1L, 2L), list(3L))),
                           list(list(list(1L), list(3L, 2L), list(1L, 2L))),
                           list(list(list(1L, 2L), list(5L))),
                           list(list(list(6L)))),
                      schema = c("sequence"))
head(spark.findFrequentSequentialPatterns(df, minSupport = 0.5, maxPatternLength = 5L))

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
df <- createDataFrame(t)
freqStats <- head(select(df, mean(df$Freq), sd(df$Freq)))
freqMean <- freqStats[1]
freqStd <- freqStats[2]

test <- spark.kstest(df, "Freq", "norm", c(freqMean, freqStd))
testSummary <- summary(test)
testSummary

## ---- eval=EVAL_CHUNK---------------------------------------------------------
t <- as.data.frame(Titanic)
training <- createDataFrame(t)
gaussianGLM <- spark.glm(training, Freq ~ Sex + Age, family = "gaussian")

# Save and then load a fitted MLlib model
modelPath <- tempfile(pattern = "ml", fileext = ".tmp")
write.ml(gaussianGLM, modelPath)
gaussianGLM2 <- read.ml(modelPath)

# Check model summary
summary(gaussianGLM2)

# Check model prediction
gaussianPredictions <- predict(gaussianGLM2, training)
head(gaussianPredictions)

unlink(modelPath)

## ---- eval=FALSE--------------------------------------------------------------
#  # Create DataFrame representing the stream of input lines from connection
#  lines <- read.stream("socket", host = hostname, port = port)
#  
#  # Split the lines into words
#  words <- selectExpr(lines, "explode(split(value, ' ')) as word")
#  
#  # Generate running word count
#  wordCounts <- count(groupBy(words, "word"))
#  
#  # Start running the query that prints the running counts to the console
#  query <- write.stream(wordCounts, "console", outputMode = "complete")

## ---- eval=FALSE--------------------------------------------------------------
#  topic <- read.stream("kafka",
#                       kafka.bootstrap.servers = "host1:port1,host2:port2",
#                       subscribe = "topic1")
#  keyvalue <- selectExpr(topic, "CAST(key AS STRING)", "CAST(value AS STRING)")

## ---- eval=FALSE--------------------------------------------------------------
#  noAggDF <- select(where(deviceDataStreamingDf, "signal > 10"), "device")
#  
#  # Print new data to console
#  write.stream(noAggDF, "console")
#  
#  # Write new data to Parquet files
#  write.stream(noAggDF,
#               "parquet",
#               path = "path/to/destination/dir",
#               checkpointLocation = "path/to/checkpoint/dir")
#  
#  # Aggregate
#  aggDF <- count(groupBy(noAggDF, "device"))
#  
#  # Print updated aggregations to console
#  write.stream(aggDF, "console", outputMode = "complete")
#  
#  # Have all the aggregates in an in memory table. The query name will be the table name
#  write.stream(aggDF, "memory", queryName = "aggregates", outputMode = "complete")
#  
#  head(sql("select * from aggregates"))

## ---- echo=FALSE, eval=EVAL_CHUNK---------------------------------------------
sparkR.session.stop()

## ----cleanup, include=FALSE, eval=EVAL_CHUNK----------------------------------
SparkR:::uninstallDownloadedSpark()

