package com.ssafy.spot.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ssafy.spot.dto.TourSight;
import com.ssafy.spot.mapper.TourSightMapper;

@Repository
public class TourSightDaoImpl implements TourSightDao {
	
	@Autowired
	TourSightMapper mapper;
	
	@Override
	public TourSight findById(String id) {
		return mapper.findById(id);
	}
	
	@Override
	public List<TourSight> findByName(String name) {
		return mapper.findByName(name);
	}
	
	@Override
	public List<TourSight> findByRange(String latitude, String longitude, String range) {
		return mapper.findByRange(latitude, longitude, range);
	}

	@Override
	public List<String> findImage(String id) {
		return mapper.findImage(id);
	}

	@Override
	public List<TourSight> tourSightList(String area) {
		return mapper.tourSightList(area);
	}

	@Override
	public String findOneImage(String id) {
		return mapper.findOneImage(id);
	}
}
