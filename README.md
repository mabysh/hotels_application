# Hotel manager application

This simple Spring-Vaadin web application allow users to view list of hotels and their categories, edit them, add new ones and delete hotels/categories from list. Besides users may filter hotels by name or address.

![](https://pp.userapi.com/c837533/v837533245/3a8b0/ZVaCZdRrZX0.jpg)
![](https://pp.userapi.com/c837533/v837533245/3a8ba/p4RBNWRNgac.jpg)
![](https://pp.userapi.com/c837533/v837533245/3a8c4/HYYZpEjlpgc.jpg)


Technologies used: Vaadin Framework, Spring, Hibernate, MySql, Maven, Jetty, Liquibase.

Deployment how to:
1. Create MySql database named demo_hotels
2. Create mysql user (Name: demo, Pass: demo) and grant full privileges.
3. git clone -b task4 https://github.com/mabysh/hotels_application
4. mvn clean install
5. mvn liquibase:dropAll
6. mvn liquibase:update
7. mvn jetty:run

In your browser: localhost:8080
