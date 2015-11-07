# A0126120Breused
###### src\tests\CommandTest.java
``` java
	@Before
	public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
	   Field instance = CommandController.class.getDeclaredField("commandController");
	   instance.setAccessible(true);
	   instance.set(null, null);
	}
	
```
###### src\tests\CommandTest.java.orig
``` orig
	@Before
	public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
	   Field instance = CommandController.class.getDeclaredField("commandController");
	   instance.setAccessible(true);
	   instance.set(null, null);
	}
	
```
