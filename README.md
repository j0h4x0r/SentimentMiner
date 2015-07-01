# SentimentMiner

Weibo (Chinese) Sentiment Analysis and Visualization

## Analysis

### Word Segmentation & Preprocessing

* [NLPIR](http://ictclas.nlpir.org/) (HMM based supervised learning) is used for Chinese word segmentation and tagging
* Remove stop words
* Naive Bayes Classifier is used for extracting useful part of speech for sentiment classification

### Feature Extraction

* LDA model to transform each document into a probability vector
* Gibbs Sampling to solve the model

### Regression Model

* SVR (Support Vector Regression) for sentiment polarity and degree
* Grid Search for parameters selection

### Publication

> Li, Di, et al. "Sentiment analysis on Weibo data." Computing, Communications and IT Applications Conference (ComComAp), 2014 IEEE. IEEE, 2014.

## Visualization

### Topic Analysis

Given a topic (keyword), return all the related tweets and their sentiment, represented as colorful bubbles. The color of the bubbles indicates the sentiment polarity of the tweets, while the size indicates the degree. Statistics are also shown in line chart.

### User Analysis

Given a user, return the count and sentiment of tweets the user posted in a specific time. line and bar chart is used to show the result.
