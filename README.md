# multi-interval-discretization
# MDL Discretization & Decision Tree (ID3)

This repository contains an implementation of the **Minimum Description Length (MDL) Discretization algorithm** proposed by **Fayyad & Irani (1993)** along with a **Decision Tree (ID3)** classifier.

The work is based on:

*  *Fayyad, U. M., & Irani, K. B. (1993). Multi-Interval Discretization of Continuous-Valued Attributes for Classification Learning.*
*  *Machine Learning Homework #3 (Spring 2013)*: MDL discretization & decision tree, assigned by Dr. Sattar Hashemi

---

##  Project Overview

* **Discretization**: Converts continuous attributes into discrete intervals using the **MDL criterion**.
* **Decision Tree**: Implements the **ID3 algorithm** using discretized features.
* **Evaluation**: Uses multiple UCI datasets and reports performance metrics.

---

##  Features

* Implementation of **Fayyad & Irani MDL discretization**
* Implementation of **ID3 Decision Tree** (without pruning, without handling missing values)
* Evaluation on **UCI toy datasets**:

  * Wine
  * Iris
  * Glass
  * Heart (and others if available)
* Performance metrics:

  * Accuracy (Train/Test)
  * Precision, Recall, F1-score (on minority class)
* Validation: **10x5-fold cross-validation**

---

##  References

* Fayyad, U. M., & Irani, K. B. (1993). *Multi-Interval Discretization of Continuous-Valued Attributes for Classification Learning.*
* Tom Mitchell. *Machine Learning*. McGraw Hill, 1997.
* UCI Machine Learning Repository.

---

Would you like me to also **add a ready-to-use table template** for the results (train/test accuracy, precision, recall, F1 per dataset) in the README, so you can just fill in your values?
