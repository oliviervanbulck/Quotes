package com.oliviervanbulck.quotes;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.*;

import redis.clients.jedis.Jedis;
@Path("/quotes")
public class QuotesResource {
	
	public void fillDb() {
		Jedis r = new Jedis("localhost");
		r.set("author:1", "Winston Churchill");
		r.set("author:2", "Albert Einstein");
		r.set("author:3", "W. C. Fields");
		r.set("author:4", "Olivier Van Bulck");
		r.set("quote:1:1", "I may be drunk, Miss, but in the morning I will be sober and you will still be ugly.");
		r.set("quote:1:2", "You have enemies? Good. That means you've stood up for something, sometime in your life.");
		r.set("quote:3:1", "I never drink water because of the disgusting things that fish do in it.");
		r.set("quote:3:2", "No doubt exists that all women are crazy; it's only a question of degree.");
		r.set("quote:2:1", "Try not to become a man of success, but rather try to become a man of value.");
		r.set("quote:2:2", "The true sign of intelligence is not knowledge but imagination.");
		r.set("quote:4:1", "The best programmers know how to use Google.");
		r.set("quote:4:2", "Children movies are what keep us young.");
		r.close();
	}
	
	@GET
	@Produces({"text/html"})
	public String getQuotes() {
		fillDb();
		Jedis jedis = new Jedis("localhost");
		String htmlString = "<html><body><ul>";
		Set<String> keys = jedis.keys("quote:*");
		for(String key : keys) {
			htmlString += "<li>" + jedis.get(key) + "</li>";
		}
		jedis.close();
		return htmlString + "</ul></body></html>";
	}
	
	@POST
	@Produces({"text/html"})
	public String getQuotesByAuthor(@FormParam("author") String author) {
		String htmlString = "<html><body><ul>";
		Jedis jedis = new Jedis("localhost");
		String authorId = "";
		Set<String> quotesId = new HashSet<String>();
		Set<String> keys = jedis.keys("author:*");
		for(String key : keys) {
			if(jedis.get(key).equals(author)) {
				authorId = key.split(":")[1];
				quotesId = jedis.keys("quote:" + authorId + ":*");
			}
		}
		
		for(String k : quotesId) {
			htmlString += "<li>" + jedis.get(k) + "</li>";
		}
		
		jedis.close();
		return htmlString + "</ul></body></html>";
	}
}
