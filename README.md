# seleniumFwk
this java maven project framework helps you to start easily Page Object Model selenium automation for your web application.
Create class for each page of your application in package "pageObject". Look at the example. Add your dom elements as attributes and code the methods using Element or Grid or Form classes.
Create your tests in package "tests", extended BaseTest by calling méthods of your pages class.
Change the runEnv in BaseTest class, modify your app properties in resources/test_xx.properties and TestProperties.class in order to load them correctly, and your tests.
