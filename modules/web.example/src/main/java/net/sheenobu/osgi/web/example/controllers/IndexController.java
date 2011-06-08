package net.sheenobu.osgi.web.example.controllers;

import java.io.IOException;
import java.io.Writer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(method=RequestMethod.GET)
	public void index(Writer os) throws IOException
	{
		os.write("Hello World From an OSGI managed controller called " + IndexController.class.getCanonicalName());
	}
	
	@RequestMapping(value="/2", method=RequestMethod.GET)
	public String two() throws IOException
	{
		return "two";
	}
	
	
}
