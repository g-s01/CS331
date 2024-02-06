# CS331: Programming Languages Lab

## Lab-4

### Simulating a Database

#### Running the file

Run the following commands in the terminal of the root folder of the project: 

```
javac Main.java
java Main
```

#### Assumptions

* Four operations are supported namely:
    * Read on `users`
    * Read on `orders`
    * Write on `users`
    * Write on `orders`

* The transactions are generated randomly out of the above four

#### Input

1. Initial size of the `users` database.
2. Initial size of the `orders` database.
3. Number of transactions on the database.

![Input](/assets/input.png)

Fig. 1: Sample Input

#### Output

The output consists of: 

1. Simulation of Read on `users`
2. Simulation of Read on `orders`
3. Simulation of Write on `users`
4. Simulation of Write on `orders`

![Initialization of the database](/assets/db-init.png)

Fig. 2: Initialization of the `users` and `orders` database

![Transactions on the users database](/assets/user-output.png)

Fig. 3: Transaction on the `users` database

![Transaction on the orders database](/assets/order-output.png)

Fig. 4: Transaction on the `orders` database

# Note

* I store the entries in a hash-table, like a normal database does
* While reading/writing on any hash-table, a lock is applied on the whole table

Credits - [Gautam Sharma](https://g-s01.github.io/)