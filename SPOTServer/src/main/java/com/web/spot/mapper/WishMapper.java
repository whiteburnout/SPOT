package com.web.spot.mapper;

import java.util.HashMap;
import java.util.List;

public interface WishMapper {
	public void addwish(String user_id, String spot_id);
	public HashMap<String, String> wishcheck(String user_id, String spot_id);
	public void delwish(String wishlist_id);
	public List<HashMap<String, String>> wishlist(String user_id);
}
