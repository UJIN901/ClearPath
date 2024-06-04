package com.ll.clearpath.domain.weather.weather.service;

import com.ll.clearpath.domain.tourlist.tourlist.entity.Tourlist;
import com.ll.clearpath.domain.tourlist.tourlist.repository.TourlistRepository;
import com.ll.clearpath.domain.weather.weather.dto.WeatherDetailDto;
import com.ll.clearpath.domain.weather.weather.dto.WeatherRequestDto;
import com.ll.clearpath.domain.weather.weather.dto.WeatherResponseDto;
import com.ll.clearpath.domain.weather.weather.dto.WeatherUpdateDto;
import com.ll.clearpath.global.util.CoordinateConverter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WeatherService {

    @Value("${maps.weather.serviceKey}")
    private String weatherServiceKey;

    private final TourlistRepository tourlistRepository;
    private final RestTemplate restTemplate;
    private final EntityManager entityManager;

    public void updateWeather() {
        List<Tourlist> tourlists = tourlistRepository.findAll(); // 상위 1개의 Tourlist만 가져오기
        for (Tourlist tourlist : tourlists) {
            CoordinateConverter.LatXLngY grid = CoordinateConverter.convertGRID_GPS(CoordinateConverter.TO_GRID, tourlist.getLatitude(), tourlist.getLongitude());
            int nx = (int) grid.x;
            int ny = (int) grid.y;

            WeatherRequestDto weatherRequestDto = createWeatherRequestDto(nx, ny);
            updateWeatherData(weatherRequestDto, tourlist);
        }
    }

    @Transactional
    public void updateWeatherData(WeatherRequestDto weatherRequestDto, Tourlist tourlist) {
        try {
            String apiUrl = String.format(
                    "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey=%s&pageNo=%d&numOfRows=%d&base_date=%s&base_time=%s&nx=%d&ny=%d&dataType=%s",
                    weatherRequestDto.getServiceKey(), weatherRequestDto.getPageNo(),
                    weatherRequestDto.getNumOfRows(), weatherRequestDto.getBase_date(), weatherRequestDto.getBase_time(),
                    weatherRequestDto.getNx(), weatherRequestDto.getNy(), weatherRequestDto.getDataType()
            );

            URI uri = new URI(apiUrl);

            restTemplate.getInterceptors().add((request, body, execution) -> {
                ClientHttpResponse response = execution.execute(request, body);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return response;
            });

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<WeatherResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, WeatherResponseDto.class);
            WeatherResponseDto weatherResponseDto = response.getBody();

            if (weatherResponseDto == null) {
                throw new IllegalStateException("API 응답이 null입니다.");
            }
            log.info(weatherResponseDto.getResponse().getHeader().getResultMsg() + " " + weatherResponseDto.getResponse().getHeader().getResultCode());

            if ("NORMAL_SERVICE".equals(weatherResponseDto.getResponse().getHeader().getResultMsg()) && "0".equals(String.valueOf(weatherResponseDto.getResponse().getHeader().getResultCode()))) {
                if (weatherResponseDto.getResponse().getBody().getItems().getItem() == null || weatherResponseDto.getResponse().getBody().getItems().getItem().isEmpty()) {
                    throw new IllegalStateException("API 응답에 항목이 없습니다.");
                }

                double currentTemperature = 0.0;
                int skyCondition = -1;
                int precipitationCondition = -1;

                String adjustedBaseTime = adjustBaseTime(weatherRequestDto.getBase_time());

                List<WeatherDetailDto> items = weatherResponseDto.getResponse().getBody().getItems().getItem();
                for (WeatherDetailDto item : items) {
                    log.debug("Weather Item: {}", item); // 개별 아이템 확인용 로그

                    if ("T1H".equals(item.getCategory()) && adjustedBaseTime.equals(item.getFcstTime())) {
                        currentTemperature = Double.parseDouble(item.getFcstValue());
                    }
                    if ("SKY".equals(item.getCategory()) && adjustedBaseTime.equals(item.getFcstTime())) {
                        skyCondition = Integer.parseInt(item.getFcstValue());
                    }
                    if ("PTY".equals(item.getCategory()) && adjustedBaseTime.equals(item.getFcstTime())) {
                        precipitationCondition = Integer.parseInt(item.getFcstValue());
                    }
                }

                log.info("Tourlist ID: {}, Current Temperature: {}, Weather Condition: {}", tourlist.getId(), currentTemperature, calculateWeatherCondition(skyCondition, precipitationCondition));
                WeatherUpdateDto weatherUpdateDto = new WeatherUpdateDto();
                weatherUpdateDto.setCurrentTemperature(currentTemperature);
                weatherUpdateDto.setWeatherCondition(calculateWeatherCondition(skyCondition, precipitationCondition));

                log.info("Tourlist ID: {}, Current Temperature: {}, Weather Condition: {}", tourlist.getId(), currentTemperature, calculateWeatherCondition(skyCondition, precipitationCondition));
                tourlist.updateWeather(weatherUpdateDto);
                log.info("Before saving tourlist: {}", tourlist);
                tourlistRepository.save(tourlist); // 변경된 엔티티를 명시적으로 저장
                entityManager.flush(); // 변경사항을 강제로 플러시
                entityManager.refresh(tourlist); // 엔티티를 새로고침하여 영속성 컨텍스트에 반영
                log.info("After saving tourlist: {}", tourlist);
            }
        } catch (URISyntaxException e) {
            log.error("URI Syntax Exception", e);
            throw new IllegalStateException("날씨 데이터를 업데이트하는 중 오류 발생: ", e);
        } catch (Exception e) {
            log.error("Error updating weather data", e);
            throw new IllegalStateException("날씨 데이터를 업데이트하는 중 오류 발생: ", e);
        }
    }

    private String calculateWeatherCondition(int skyCondition, int precipitationCondition) {
        if (precipitationCondition == 0) {
            switch (skyCondition) {
                case 1: return "맑음";
                case 3: return "구름많음";
                case 4: return "흐림";
            }
        } else if (precipitationCondition == 1 || precipitationCondition == 5 || precipitationCondition == 6) {
            return "흐리고 비";
        } else if (precipitationCondition == 2 || precipitationCondition == 3 || precipitationCondition == 7) {
            return "눈";
        }
        return "알 수 없음";
    }

    private WeatherRequestDto createWeatherRequestDto(int nx, int ny) {
        WeatherRequestDto weatherRequestDto = new WeatherRequestDto();
        weatherRequestDto.setServiceKey(weatherServiceKey);
        weatherRequestDto.setPageNo(1);
        weatherRequestDto.setNumOfRows(60);
        weatherRequestDto.setDataType("JSON");

        LocalDateTime now = LocalDateTime.now();
        weatherRequestDto.setBase_date(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        LocalDateTime adjustedTime = now.minusHours(1).withMinute(0);
        weatherRequestDto.setBase_time(adjustedTime.format(DateTimeFormatter.ofPattern("HHmm")));

        weatherRequestDto.setNx(nx);
        weatherRequestDto.setNy(ny);

        return weatherRequestDto;
    }

    private String adjustBaseTime(String baseTime) {
        int hour = Integer.parseInt(baseTime.substring(0, 2));
        hour += 1; // 1시간 더하기
        if (hour >= 24) {
            hour = 0; // 24시를 넘기면 0시로 변경
        }
        return String.format("%02d00", hour); // 정각으로 설정
    }
}
