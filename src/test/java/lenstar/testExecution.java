package lenstar;

import org.testng.annotations.Test;

public class testExecution {
	
	@Test
	public void testcheck()
	{
		Root r1 = new Root();
		r1.setType("transaction");
		r1.setResourceType("Observation/20230428145324-08001");
		
		
		System.out.println(r1.getResourceType());
		System.out.println(r1.getType());
		
	}

}
