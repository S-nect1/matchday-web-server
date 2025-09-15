package com.matchday.modules.match.application;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressParsingService {
    
    private static final Pattern CITY_DISTRICT_PATTERN = Pattern.compile("([가-힣]+특별시|[가-힣]+광역시|[가-힣]+특별자치시|[가-힣]+특별자치도|[가-힣]+도)\\s*([가-힣]+구|[가-힣]+군|[가-힣]+시)");
    
    public AddressInfo parseAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("주소가 비어있습니다.");
        }
        
        Matcher matcher = CITY_DISTRICT_PATTERN.matcher(address);
        
        if (!matcher.find()) {
            throw new IllegalArgumentException("잘못된 주소입니다." + address);
        }
        
        String cityStr = matcher.group(1);
        String districtStr = matcher.group(2);
        
        City city = City.fromKoreanName(cityStr);
        District district = District.fromKoreanName(districtStr, city);
        
        return new AddressInfo(city, district);
    }
    
    public static class AddressInfo {
        private final City city;
        private final District district;
        
        public AddressInfo(City city, District district) {
            this.city = city;
            this.district = district;
        }
        
        public City getCity() {
            return city;
        }
        
        public District getDistrict() {
            return district;
        }
    }
}