package com.matchday.modules.match.application;

import com.matchday.common.entity.enums.City;
import com.matchday.common.entity.enums.District;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class AddressParsingServiceTest {

    @Autowired
    private AddressParsingService addressParsingService;

    @Test
    @DisplayName("서울특별시 강남구 주소 파싱 성공")
    void parseAddress_Seoul_Gangnam_Success() {
        // given
        String address = "서울특별시 강남구 테헤란로 427";
        
        // when
        AddressParsingService.AddressInfo result = addressParsingService.parseAddress(address);
        
        // then
        assertThat(result.getCity()).isEqualTo(City.SEOUL);
        assertThat(result.getDistrict()).isEqualTo(District.SEOUL_GANGNAM);
    }

    @Test
    @DisplayName("경기도 수원시 주소 파싱 성공")
    void parseAddress_Gyeonggi_Suwon_Success() {
        // given
        String address = "경기도 수원시 영통구 월드컵로 206";
        
        // when
        AddressParsingService.AddressInfo result = addressParsingService.parseAddress(address);
        
        // then
        assertThat(result.getCity()).isEqualTo(City.GYEONGGI);
        assertThat(result.getDistrict()).isEqualTo(District.GYEONGGI_SUWON);
    }

    @Test
    @DisplayName("부산광역시 해운대구 주소 파싱 성공")
    void parseAddress_Busan_Haeundae_Success() {
        // given
        String address = "부산광역시 해운대구 해운대해변로 264";
        
        // when
        AddressParsingService.AddressInfo result = addressParsingService.parseAddress(address);
        
        // then
        assertThat(result.getCity()).isEqualTo(City.BUSAN);
        assertThat(result.getDistrict()).isEqualTo(District.BUSAN_HAEUNDAE);
    }

    @Test
    @DisplayName("빈 주소 입력 시 예외 발생")
    void parseAddress_EmptyAddress_ThrowsException() {
        // given
        String address = "";
        
        // when & then
        assertThatThrownBy(() -> addressParsingService.parseAddress(address))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주소가 비어있습니다.");
    }

    @Test
    @DisplayName("null 주소 입력 시 예외 발생")
    void parseAddress_NullAddress_ThrowsException() {
        // given
        String address = null;
        
        // when & then
        assertThatThrownBy(() -> addressParsingService.parseAddress(address))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주소가 비어있습니다.");
    }

    @Test
    @DisplayName("잘못된 주소 형식 시 예외 발생")
    void parseAddress_InvalidFormat_ThrowsException() {
        // given
        String address = "잘못된 주소 형식";
        
        // when & then
        assertThatThrownBy(() -> addressParsingService.parseAddress(address))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잘못된 주소입니다." + address);
    }

    @Test
    @DisplayName("지원하지 않는 시/도 입력 시 예외 발생")
    void parseAddress_UnsupportedCity_ThrowsException() {
        // given
        String address = "미지원도 테스트구 테스트로 123";
        
        // when & then
        assertThatThrownBy(() -> addressParsingService.parseAddress(address))
            .isInstanceOf(IllegalArgumentException.class);
    }
}