# ShopKeeper
Demo Shopkeeper project, using Spring Boot, Spring REST, Spring Security.

Written using Java11 and Spring Boot 2.2.6. 

Data storage implemented with In-memory H2 database.

Tests with JUnit 4.13. 

REST methods comply with HATEOAS 1.0.4 

Application is done as a simple MVC pattern.

For the purpose of simple demo, Spring Security was implemented as HTTP Basic authentication. 
It is definitely has to be replaced with much more secure OAuth2 method, but for the purpose of demo HTTP Basic auth is sufficient.

Application has two preconfigured users - USER and ADMIN, with roles of 'USER' and 'ADMIN'.
There is a number of endpoints:
- /items 

     - GET
     - USER role 
     - returns list of available items. Sold-out items are skipped.
     
- /items/{id}
     - GET
     - USER role 
     - returns single item by item Id. Sold-out item returns error message.
- /itemsDetailed
     - GET
     - ADMIN role 
     - returns list of all items in detailed form, including basePrice and quantity (hidden for user-accessible methods).
- /items 
     - POST
     - ADMIN role 
     - creates new item. Item data in Json format in request body.
- /buy/{id}
     - POST
     - USER role 
     - Executes purchase. Sold-out item returns error message. Reduces quantity by one. 
- /items/{id}
     - PUT
     - ADMIN role 
     - Updates existing item (or creates new onbe). Item data in Json format in request body.
- /items/{id}
     - DELETE
     - ADMIN role 
     - Deletes item.

## Installation

Create an empty folder in the location of your choice. 
Open it in the command shell and execute:

```bash
git clone https://github.com/andreiGl/ShopKeeper.git
cd ShopKeeper
mvn test
mvn clean package
```

## Usage
To start the application:
```bash
mvn spring-boot:run
```

Included is the Postman test collection JSON file with all available REST methods to test:
```
Shopkeeper Test.postman_collection.json
```

But regular CURL command-line testing is also available.


A normal calls to GET and POST will return a 401 error, because all endpoints are protected, and need authentication.

```bash
curl localhost:8080/items

<h2>Error Page</h2><div>Status code: <b>401</b></div>
```

Send a GET request with user login:
```bash
curl localhost:8080/items -u user:password
```
```
{
    "_embedded": {
        "itemSummaryList": [
            {
                "id": 1,
                "name": "Design Patterns: Elements of Reusable Object-Oriented Software",
                "description": "This classical book is critical reading to really understand what design patterns are and become familiar with the most common design patterns you are likely to encounter in your career.",
                "price": 12,
                "_links": {
                    "self": [
                        {
                            "href": "http://localhost:8080/items/1"
                        },
                        {
                            "href": "http://localhost:8080/buy/1"
                        }
                    ]
                }
            },
            {
                "id": 2,
                "name": "Apple MacBook Pro",
                "description": "16-inch, 64Gb, 8Tb",
                "price": 3499,
                "_links": {
                    "self": [
                        {
                            "href": "http://localhost:8080/items/2"
                        },
                        {
                            "href": "http://localhost:8080/buy/2"
                        }
                    ]
                }
            },
            {
                "id": 3,
                "name": "Lenovo Thinkpad P73",
                "description": "17-inch, 32Gb, 1Tb",
                "price": 5759,
                "_links": {
                    "self": [
                        {
                            "href": "http://localhost:8080/items/3"
                        },
                        {
                            "href": "http://localhost:8080/buy/3"
                        }
                    ]
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/items"
        }
    }
}
```

Similarly:
```bash
curl localhost:8080/itemsDetailed -u admin:password
```

```bash
curl localhost:8080/items/1 -u user:password
```

```bash
curl -X POST localhost:8080/buy/1 -u user:password
```

```bash
curl -X POST localhost:8080/items/9 -u admin:password -H "Content-type:application/json" 
	-d {\"name\":\"Book9\",\"description\":\"Description9\",\"price\":\"28\",\"quantity\":\"5\"}
```
```
{
    "id": 4,
    "name": "Book9",
    "description": "Description9",
    "price": 28,
    "quantity": 5,
    "basePrice": 0,
    "surgeTime": null,
    "views": null,
    "_links": {
        "self": [
            {
                "href": "http://localhost:8080/items/9"
            },
            {
                "href": "http://localhost:8080/buy/9"
            }
        ],
        "items": {
            "href": "http://localhost:8080/items"
        }
    }
}
```


## Implementation Details
Implemented a surge-price mechanism:
- if item is accessed more than 10 times (11 or more times) in one hour, it's price being increased/surged by 10% (both values configurable). 
- each subsequent access to the item refreshes surge status. 
- surged price goes back to basse price in one hour, unless there was enough activity (11 or more times) to prolong surge price.

To keep track of surge price changes, each Item type has a Queue field, where recent item-access'es timestamps are stored. Queue is limited in max size (11 elements). Queue functionality is implemented with ArrayList type. 

As per requirements document, price is of type integer, and does not have a fractions.



