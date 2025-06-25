# COMP 5348 Project

# Table of Contents

- **[Overview](#Overview)**
- **[System Architecture](#System-Architecture)**
  - **[System Architecture Diagram](#System-Architecture-Diagram)**
- **[Project Structure](#Project-Structure)**
- **[Installation and Setup](#Installation-and-Setup)**
- **[Run the Application](#Run-the-Application)**
- **[Dispatch Order - Important](#Dispatch-Order---Important)**
- **[Service Description](#Service-Description)**
  - **[Bank](#Bank)**
  - **[DeliveryCo](#DeliveryCo)**
  - **[Email](#Email)**

# Overview

The project implements a complete BM online store system for selling electronic products, which includes several different services that can handle various different aspects of business operations, including order processing, payment processing, delivery management, warehouse management and email notifications.

# System Architecture

The system consists of 6 main components.

**For backend side, there are 5 projects:**

- [**backend**](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/tree/main/backend) - store’s backend service, the frontend will only interact with this project.
- [**bank**](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/tree/main/bank) - bank service provide ability to make a transaction.
- [**deliveryCo**](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/tree/main/deliveryCo) - delivery service provide ability to delivery products to the customer.
- [**email**](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/tree/main/email) - email service provide ability to stimulate send email notifications.
- [**warehouse**](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/tree/main/warehouse) - warehouse service provide ability to store the product.

**For frontend side, there is only 1 project**:

- [**front-end-ui**](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/tree/main/front-end-ui) - basic frontend ui code, provide ability to customer to interact with our online store website.

Our service architecture utilizes RESTful APIs to manage interactions and data flows. To ensure fault tolerance and handle specific tasks such as email notifications and delivery status updates, we have adopted RabbitMQ. This setup enables us to improve the reliability and efficiency of services, effectively decouple system components, and ensure reliable handling of critical messages even in the event of partial system failures.

## System Architecture Diagram

   ![System Architecture.png](System%20Architecture.png)

# Project Structure

```
backend/
├───.mvn
│   └───wrapper
├───src
│   ├───main
│   │   ├───java
│   │   │   └───com
│   │   │       └───usyd
│   │   │           └───backend
│   │   │               ├───configs
│   │   │               ├───controller
│   │   │               ├───dto
│   │   │               ├───model
│   │   │               │   ├───externalRequest
│   │   │               │   ├───externalResponse
│   │   │               │   ├───request
│   │   │               │   └───response
│   │   │               ├───repository
│   │   │               ├───service
│   │   │               │   └───impl
│   │   │               └───utils
│   │   │                   └───enums
│   │   └───resources
│   │       └───static
│   │           └───uploads
│   └───test
│       └───java
│           └───com
│               └───usyd
│                   └───backend
└───target
    ├───classes
    │   ├───com
    │   │   └───usyd
    │   │       └───backend
    │   │           ├───configs
    │   │           ├───controller
    │   │           ├───dto
    │   │           ├───model
    │   │           │   ├───externalRequest
    │   │           │   ├───externalResponse
    │   │           │   ├───request
    │   │           │   └───response
    │   │           ├───repository
    │   │           ├───service
    │   │           │   ├───impl
    │   │           │   └───messaging
    │   │           └───utils
    │   │               └───enums
    │   └───static
    │       └───uploads
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    └───test-classes
        └───com
            └───usyd
                └───backend
```

# Installation and Setup

In order to set up the project, please follow below steps:

1. **Database setup**

   Please create the following databases in the `Postgres`:
   - bank
   - delivery
   - store
   - warehouse
   
   If you want to use test data, please check the `DB script` folder and run each SQL script on the corresponding database. It contains following script:
   - bank.sql
   - delivery.sql
   - store.sql
   - warehouse.sql

2. **RabbitMQ set up**

   Please make sure `Docker` is installed and `RabbitMQ` runs with the following configuration:
   
   ```
   Host: localhost
   Port: 5672
   Username: admin
   Password: admin
   ```
   
3. **Service configuration**

   For each service (backend, bank, deliveryCo, email, warehouse), please update: `application.properties`

   ```
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:{port number}/{database name}
   spring.datasource.username={postgres connection username}
   spring.datasource.password={postgres connection password}
   
   # RabbitMQ Configuration (In the `backend` project’s `application.properties`)
   spring.rabbitmq.host=localhost
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=admin
   spring.rabbitmq.password=admin
   ```

   For backend project, in the `application.properties` there is field 
   
   ```
   comp5348.project.admin.account=17296848984629657
   ```
   
   This field specifies the admin (store owner's) account, which is the account to which funds are transferred whenever a customer purchases a product. The default administrator account of the system is `17296848984629657` (also the bank account of the recipient). `Make sure you update this config if the admin (store owner's) account is changed`

4. **Front-end setup**

   Please ensure that the following front-end Settings are completed on the terminal:

   ```
   cd front-end-ui
   npm install
   ```

# Run the Application

1. Start `RabbitMQ` by running the following code in the terminal in `Docker`:

   ```
   rabbitmq-plugins enable rabbitmq_management 
   ```

2. Please start each backend service individually (`BackendApplication`, `BankApplication`, `DeliveryCoApplication`, `EmailApplication` and `WarehouseApplication`).

3. Please run the following code to start the front end.

   ```
   cd front-end-ui
   npm install
   npm start
   ```
4. Please check `COMP5348 group project.postman_collection.json` file to import request collection into Postman.

If you encounter any problems during setup or while running the application, please feel free to contact us.

- Yihang Liu - yliu0826@uni.sydney.edu.au
- Yi Xu - yixu4396@uni.sydney.edu.au
- Yuanhua Zhong - yzho0240@uni.sydney.edu.au

# Dispatch Order - Important

**Please Note**: 

1. After a customer places an order and completes payment, the admin (store owner) must manually dispatch the order using the following API endpoint:

   ```
   http://localhost:8080/comp5348/order/dispatch/{orderId}
   ```
   
2. Ensure that the **front end** must run on the `3000` port number.
   
3. The back-end ports are as follows:
   - **backend**: `8080`
   - **bank**: `6888`
   - **deliveryCo**: `1888`
   - **email**: `5888`

# Service Description

## Bank
   - Create an account
   - Query Account
   - Account Top-up
   - Create a Transaction
   - Refund Transaction
   - Perform a Refund Operation
   - Query a Single Transaction
   - Query All Transaction on an Account

Please refer to this [wiki](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/wiki/Bank) for more detailed information

## DeliveryCo
   - Create a Delivery Order
   - Create batch Delivery Orders
   - Query Individual Delivery Order
   - Query All Delivery Orders of the User

Please refer to this [wiki](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/wiki/deliveryCo) for more detailed information

## Email
   - Send Email

Please refer to this [wiki](https://github.sydney.edu.au/COMP5348-projects/Practice-06-Group-01/wiki/Email-service) for more detailed information
