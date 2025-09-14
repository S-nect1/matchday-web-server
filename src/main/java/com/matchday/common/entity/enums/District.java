package com.matchday.common.entity.enums;

public enum District {
    // 서울특별시 (25개구)
    SEOUL_GANGNAM("강남구", City.SEOUL),
    SEOUL_GANGDONG("강동구", City.SEOUL),
    SEOUL_GANGBUK("강북구", City.SEOUL),
    SEOUL_GANGSEO("강서구", City.SEOUL),
    SEOUL_GWANAK("관악구", City.SEOUL),
    SEOUL_GWANGJIN("광진구", City.SEOUL),
    SEOUL_GURO("구로구", City.SEOUL),
    SEOUL_GEUMCHEON("금천구", City.SEOUL),
    SEOUL_NOWON("노원구", City.SEOUL),
    SEOUL_DOBONG("도봉구", City.SEOUL),
    SEOUL_DONGDAEMUN("동대문구", City.SEOUL),
    SEOUL_DONGJAK("동작구", City.SEOUL),
    SEOUL_MAPO("마포구", City.SEOUL),
    SEOUL_SEODAEMUN("서대문구", City.SEOUL),
    SEOUL_SEOCHO("서초구", City.SEOUL),
    SEOUL_SEONGDONG("성동구", City.SEOUL),
    SEOUL_SEONGBUK("성북구", City.SEOUL),
    SEOUL_SONGPA("송파구", City.SEOUL),
    SEOUL_YANGCHEON("양천구", City.SEOUL),
    SEOUL_YEONGDEUNGPO("영등포구", City.SEOUL),
    SEOUL_YONGSAN("용산구", City.SEOUL),
    SEOUL_EUNPYEONG("은평구", City.SEOUL),
    SEOUL_JONGNO("종로구", City.SEOUL),
    SEOUL_JUNG("중구", City.SEOUL),
    SEOUL_JUNGNANG("중랑구", City.SEOUL),
    
    // 부산광역시 (16개구군)
    BUSAN_GANGSEO("강서구", City.BUSAN),
    BUSAN_GEUMJEONG("금정구", City.BUSAN),
    BUSAN_GIJANG("기장군", City.BUSAN),
    BUSAN_NAM("남구", City.BUSAN),
    BUSAN_DONG("동구", City.BUSAN),
    BUSAN_DONGNAE("동래구", City.BUSAN),
    BUSAN_BUSANJIN("부산진구", City.BUSAN),
    BUSAN_BUK("북구", City.BUSAN),
    BUSAN_SASANG("사상구", City.BUSAN),
    BUSAN_SAHA("사하구", City.BUSAN),
    BUSAN_SEO("서구", City.BUSAN),
    BUSAN_SUYEONG("수영구", City.BUSAN),
    BUSAN_YEONJE("연제구", City.BUSAN),
    BUSAN_YEONGDO("영도구", City.BUSAN),
    BUSAN_JUNG("중구", City.BUSAN),
    BUSAN_HAEUNDAE("해운대구", City.BUSAN),
    
    // 대구광역시 (8개구군)
    DAEGU_NAM("남구", City.DAEGU),
    DAEGU_DALSEO("달서구", City.DAEGU),
    DAEGU_DALSEONG("달성군", City.DAEGU),
    DAEGU_DONG("동구", City.DAEGU),
    DAEGU_BUK("북구", City.DAEGU),
    DAEGU_SEO("서구", City.DAEGU),
    DAEGU_SUSEONG("수성구", City.DAEGU),
    DAEGU_JUNG("중구", City.DAEGU),
    
    // 인천광역시 (10개구군)
    INCHEON_GANGHWA("강화군", City.INCHEON),
    INCHEON_GYEYANG("계양구", City.INCHEON),
    INCHEON_NAMDONG("남동구", City.INCHEON),
    INCHEON_DONG("동구", City.INCHEON),
    INCHEON_MICHUHOL("미추홀구", City.INCHEON),
    INCHEON_BUPYEONG("부평구", City.INCHEON),
    INCHEON_SEO("서구", City.INCHEON),
    INCHEON_YEONSU("연수구", City.INCHEON),
    INCHEON_ONGJIN("옹진군", City.INCHEON),
    INCHEON_JUNG("중구", City.INCHEON),
    
    // 광주광역시 (5개구)
    GWANGJU_GWANGSAN("광산구", City.GWANGJU),
    GWANGJU_NAM("남구", City.GWANGJU),
    GWANGJU_DONG("동구", City.GWANGJU),
    GWANGJU_BUK("북구", City.GWANGJU),
    GWANGJU_SEO("서구", City.GWANGJU),
    
    // 대전광역시 (5개구)
    DAEJEON_DAEDEOK("대덕구", City.DAEJEON),
    DAEJEON_DONG("동구", City.DAEJEON),
    DAEJEON_SEO("서구", City.DAEJEON),
    DAEJEON_YUSEONG("유성구", City.DAEJEON),
    DAEJEON_JUNG("중구", City.DAEJEON),
    
    // 울산광역시 (5개구군)
    ULSAN_NAM("남구", City.ULSAN),
    ULSAN_DONG("동구", City.ULSAN),
    ULSAN_BUK("북구", City.ULSAN),
    ULSAN_ULJU("울주군", City.ULSAN),
    ULSAN_JUNG("중구", City.ULSAN),
    
    // 세종특별자치시
    SEJONG_SEJONG("세종시", City.SEJONG),
    
    // 경기도 (31개시군)
    GYEONGGI_GAPYEONG("가평군", City.GYEONGGI),
    GYEONGGI_GOYANG("고양시", City.GYEONGGI),
    GYEONGGI_GWACHEON("과천시", City.GYEONGGI),
    GYEONGGI_GWANGMYEONG("광명시", City.GYEONGGI),
    GYEONGGI_GWANGJU("광주시", City.GYEONGGI),
    GYEONGGI_GURI("구리시", City.GYEONGGI),
    GYEONGGI_GUNPO("군포시", City.GYEONGGI),
    GYEONGGI_GIMPO("김포시", City.GYEONGGI),
    GYEONGGI_NAMYANGJU("남양주시", City.GYEONGGI),
    GYEONGGI_DONGDUCHEON("동두천시", City.GYEONGGI),
    GYEONGGI_BUCHEON("부천시", City.GYEONGGI),
    GYEONGGI_SEONGNAM("성남시", City.GYEONGGI),
    GYEONGGI_SUWON("수원시", City.GYEONGGI),
    GYEONGGI_SIHEUNG("시흥시", City.GYEONGGI),
    GYEONGGI_ANSAN("안산시", City.GYEONGGI),
    GYEONGGI_ANSEONG("안성시", City.GYEONGGI),
    GYEONGGI_ANYANG("안양시", City.GYEONGGI),
    GYEONGGI_YANGJU("양주시", City.GYEONGGI),
    GYEONGGI_YANGPYEONG("양평군", City.GYEONGGI),
    GYEONGGI_YEOJU("여주시", City.GYEONGGI),
    GYEONGGI_YEONCHEON("연천군", City.GYEONGGI),
    GYEONGGI_OSAN("오산시", City.GYEONGGI),
    GYEONGGI_YONGIN("용인시", City.GYEONGGI),
    GYEONGGI_UIWANG("의왕시", City.GYEONGGI),
    GYEONGGI_UIJEONGBU("의정부시", City.GYEONGGI),
    GYEONGGI_ICHEON("이천시", City.GYEONGGI),
    GYEONGGI_PAJU("파주시", City.GYEONGGI),
    GYEONGGI_PYEONGTAEK("평택시", City.GYEONGGI),
    GYEONGGI_POCHEON("포천시", City.GYEONGGI),
    GYEONGGI_HANAM("하남시", City.GYEONGGI),
    GYEONGGI_HWASEONG("화성시", City.GYEONGGI),
    
    // 강원특별자치도 (18개시군)
    GANGWON_GANGNEUNG("강릉시", City.GANGWON),
    GANGWON_GOSEONG("고성군", City.GANGWON),
    GANGWON_DONGHAE("동해시", City.GANGWON),
    GANGWON_SAMCHEOK("삼척시", City.GANGWON),
    GANGWON_SOKCHO("속초시", City.GANGWON),
    GANGWON_YANGGU("양구군", City.GANGWON),
    GANGWON_YANGYANG("양양군", City.GANGWON),
    GANGWON_YEONGWOL("영월군", City.GANGWON),
    GANGWON_WONJU("원주시", City.GANGWON),
    GANGWON_INJE("인제군", City.GANGWON),
    GANGWON_JEONGSEON("정선군", City.GANGWON),
    GANGWON_CHEORWON("철원군", City.GANGWON),
    GANGWON_CHUNCHEON("춘천시", City.GANGWON),
    GANGWON_TAEBAEK("태백시", City.GANGWON),
    GANGWON_PYEONGCHANG("평창군", City.GANGWON),
    GANGWON_HONGCHEON("홍천군", City.GANGWON),
    GANGWON_HWACHEON("화천군", City.GANGWON),
    GANGWON_HOENGSEONG("횡성군", City.GANGWON),
    
    // 충청북도 (11개시군)
    CHUNGBUK_GOESAN("괴산군", City.CHUNGBUK),
    CHUNGBUK_DANYANG("단양군", City.CHUNGBUK),
    CHUNGBUK_BOEUN("보은군", City.CHUNGBUK),
    CHUNGBUK_SEOWON("서원구", City.CHUNGBUK),
    CHUNGBUK_CHEONGJU("청주시", City.CHUNGBUK),
    CHUNGBUK_CHUNGJU("충주시", City.CHUNGBUK),
    CHUNGBUK_JINCHEON("진천군", City.CHUNGBUK),
    CHUNGBUK_EUMSEONG("음성군", City.CHUNGBUK),
    CHUNGBUK_JECHEON("제천시", City.CHUNGBUK),
    CHUNGBUK_JEUNGPYEONG("증평군", City.CHUNGBUK),
    CHUNGBUK_OKCHEON("옥천군", City.CHUNGBUK),
    
    // 충청남도 (15개시군)
    CHUNGNAM_GYERYONG("계룡시", City.CHUNGNAM),
    CHUNGNAM_GONGJU("공주시", City.CHUNGNAM),
    CHUNGNAM_GEUMSAN("금산군", City.CHUNGNAM),
    CHUNGNAM_NONSAN("논산시", City.CHUNGNAM),
    CHUNGNAM_DANGJIN("당진시", City.CHUNGNAM),
    CHUNGNAM_BORYEONG("보령시", City.CHUNGNAM),
    CHUNGNAM_BUYEO("부여군", City.CHUNGNAM),
    CHUNGNAM_SEOSAN("서산시", City.CHUNGNAM),
    CHUNGNAM_SEOCHEON("서천군", City.CHUNGNAM),
    CHUNGNAM_ASAN("아산시", City.CHUNGNAM),
    CHUNGNAM_YESAN("예산군", City.CHUNGNAM),
    CHUNGNAM_CHEONAN("천안시", City.CHUNGNAM),
    CHUNGNAM_CHEONGYANG("청양군", City.CHUNGNAM),
    CHUNGNAM_TAEAN("태안군", City.CHUNGNAM),
    CHUNGNAM_HONGSEONG("홍성군", City.CHUNGNAM),
    
    // 전북특별자치도 (14개시군)
    JEONBUK_GOCHANG("고창군", City.JEONBUK),
    JEONBUK_GUNSAN("군산시", City.JEONBUK),
    JEONBUK_GIMJE("김제시", City.JEONBUK),
    JEONBUK_NAMWON("남원시", City.JEONBUK),
    JEONBUK_MUJU("무주군", City.JEONBUK),
    JEONBUK_BUAN("부안군", City.JEONBUK),
    JEONBUK_SUNCHANG("순창군", City.JEONBUK),
    JEONBUK_WANJU("완주군", City.JEONBUK),
    JEONBUK_IKSAN("익산시", City.JEONBUK),
    JEONBUK_IMSIL("임실군", City.JEONBUK),
    JEONBUK_JANGSU("장수군", City.JEONBUK),
    JEONBUK_JEONJU("전주시", City.JEONBUK),
    JEONBUK_JEONGEUP("정읍시", City.JEONBUK),
    JEONBUK_JINAN("진안군", City.JEONBUK),
    
    // 전라남도 (22개시군)
    JEONNAM_GANGJIN("강진군", City.JEONNAM),
    JEONNAM_GOHEUNG("고흥군", City.JEONNAM),
    JEONNAM_GOKSEONG("곡성군", City.JEONNAM),
    JEONNAM_GWANGYANG("광양시", City.JEONNAM),
    JEONNAM_GURYE("구례군", City.JEONNAM),
    JEONNAM_NAJU("나주시", City.JEONNAM),
    JEONNAM_DAMYANG("담양군", City.JEONNAM),
    JEONNAM_MOKPO("목포시", City.JEONNAM),
    JEONNAM_MUAN("무안군", City.JEONNAM),
    JEONNAM_BOSEONG("보성군", City.JEONNAM),
    JEONNAM_SUNCHEON("순천시", City.JEONNAM),
    JEONNAM_SINAN("신안군", City.JEONNAM),
    JEONNAM_YEOSU("여수시", City.JEONNAM),
    JEONNAM_YEONGAM("영암군", City.JEONNAM),
    JEONNAM_YEONGKWANG("영광군", City.JEONNAM),
    JEONNAM_YEONGGWANG("영광군", City.JEONNAM),
    JEONNAM_WANDO("완도군", City.JEONNAM),
    JEONNAM_JANGSEONG("장성군", City.JEONNAM),
    JEONNAM_JANGHEUNG("장흥군", City.JEONNAM),
    JEONNAM_JINDO("진도군", City.JEONNAM),
    JEONNAM_HAMPYEONG("함평군", City.JEONNAM),
    JEONNAM_HAENAM("해남군", City.JEONNAM),
    
    // 경상북도 (23개시군)
    GYEONGBUK_GYEONGSAN("경산시", City.GYEONGBUK),
    GYEONGBUK_GYEONGJU("경주시", City.GYEONGBUK),
    GYEONGBUK_GORYEONG("고령군", City.GYEONGBUK),
    GYEONGBUK_GUMI("구미시", City.GYEONGBUK),
    GYEONGBUK_GUNWI("군위군", City.GYEONGBUK),
    GYEONGBUK_GIMCHEON("김천시", City.GYEONGBUK),
    GYEONGBUK_MUNGYEONG("문경시", City.GYEONGBUK),
    GYEONGBUK_BONGHWA("봉화군", City.GYEONGBUK),
    GYEONGBUK_SANGJU("상주시", City.GYEONGBUK),
    GYEONGBUK_SEONGJU("성주군", City.GYEONGBUK),
    GYEONGBUK_ANDONG("안동시", City.GYEONGBUK),
    GYEONGBUK_YEONGDEOK("영덕군", City.GYEONGBUK),
    GYEONGBUK_YEONGYANG("영양군", City.GYEONGBUK),
    GYEONGBUK_YEONGJU("영주시", City.GYEONGBUK),
    GYEONGBUK_YEONGCHEON("영천시", City.GYEONGBUK),
    GYEONGBUK_YECHEON("예천군", City.GYEONGBUK),
    GYEONGBUK_ULLEUNG("울릉군", City.GYEONGBUK),
    GYEONGBUK_ULJIN("울진군", City.GYEONGBUK),
    GYEONGBUK_UISEONG("의성군", City.GYEONGBUK),
    GYEONGBUK_CHEONGDO("청도군", City.GYEONGBUK),
    GYEONGBUK_CHEONGSONG("청송군", City.GYEONGBUK),
    GYEONGBUK_CHILGOK("칠곡군", City.GYEONGBUK),
    GYEONGBUK_POHANG("포항시", City.GYEONGBUK),
    
    // 경상남도 (18개시군)
    GYEONGNAM_GEOJE("거제시", City.GYEONGNAM),
    GYEONGNAM_GEOCHANG("거창군", City.GYEONGNAM),
    GYEONGNAM_GOSEONG("고성군", City.GYEONGNAM),
    GYEONGNAM_GIMHAE("김해시", City.GYEONGNAM),
    GYEONGNAM_NAMHAE("남해군", City.GYEONGNAM),
    GYEONGNAM_MIRYANG("밀양시", City.GYEONGNAM),
    GYEONGNAM_SACHEON("사천시", City.GYEONGNAM),
    GYEONGNAM_SANCHEONG("산청군", City.GYEONGNAM),
    GYEONGNAM_YANGSAN("양산시", City.GYEONGNAM),
    GYEONGNAM_UIRYEONG("의령군", City.GYEONGNAM),
    GYEONGNAM_JINJU("진주시", City.GYEONGNAM),
    GYEONGNAM_CHANGNYEONG("창녕군", City.GYEONGNAM),
    GYEONGNAM_CHANGWON("창원시", City.GYEONGNAM),
    GYEONGNAM_TONGYEONG("통영시", City.GYEONGNAM),
    GYEONGNAM_HAMAN("함안군", City.GYEONGNAM),
    GYEONGNAM_HAMYANG("함양군", City.GYEONGNAM),
    GYEONGNAM_HAPCHEON("합천군", City.GYEONGNAM),
    GYEONGNAM_HADONG("하동군", City.GYEONGNAM),
    
    // 제주특별자치도 (2개시)
    JEJU_JEJU("제주시", City.JEJU),
    JEJU_SEOGWIPO("서귀포시", City.JEJU);
    
    private final String koreanName;
    private final City city;
    
    District(String koreanName, City city) {
        this.koreanName = koreanName;
        this.city = city;
    }
    
    public String getKoreanName() {
        return koreanName;
    }
    
    public City getCity() {
        return city;
    }
    
    public static District[] getDistrictsByCity(City city) {
        return java.util.Arrays.stream(values())
                .filter(district -> district.city == city)
                .toArray(District[]::new);
    }
    
    public static District fromKoreanName(String koreanName, City city) {
        for (District district : values()) {
            if (district.koreanName.equals(koreanName) && district.city == city) {
                return district;
            }
        }
        throw new IllegalArgumentException("해당하는 구/군을 찾을 수 없습니다: " + koreanName + " in " + city.getKoreanName());
    }
}
