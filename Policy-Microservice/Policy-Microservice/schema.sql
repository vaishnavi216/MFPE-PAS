CREATE TABLE Policy_Master(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    Policy_ID VARCHAR(20) NOT NULL,
    Property_Type VARCHAR(30) NOT NULL,
    Consumer_Type VARCHAR(30) NOT NULL,
    Assured_Sum VARCHAR(40) NOT NULL,
    Tenure VARCHAR(30) NOT NULL,
    Business_Value BIGINT NOT NULL,
    Property_Value BIGINT NOT NULL,
    Base_Location VARCHAR(30) NOT NULL,
    Type VARCHAR(30) NOT NULL
);