# Hotel manager application

This simple Spring-Vaadin web application allow users to view list of hotels and their categories, edit them, add new ones and delete hotels/categories from list. Besides users may filter hotels by name or address.

![](https://pp.userapi.com/c639826/v639826245/1ddff/ZjhxIPy-axw.jpg)
![](https://pp.userapi.com/c639826/v639826245/1de09/KnoB_8vY1dM.jpg)


Technologies used: Vaadin Framework, Spring, Hibernate, MySql, Maven, Jetty, Liquibase, Selenium, JUnit.

Deployment how to:
1. Create MySql database named demo_hotels
2. Create mysql user (Name: demo, Pass: demo) and grant full privileges.
3. git clone -b task5 https://github.com/mabysh/hotels_application
4. mvn clean install
5. mvn liquibase:dropAll
6. mvn liquibase:update
7. mvn jetty:run

In your browser: localhost:8080
