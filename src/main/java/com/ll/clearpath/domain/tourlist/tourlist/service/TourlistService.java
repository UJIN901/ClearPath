package com.ll.clearpath.domain.tourlist.tourlist.service;

import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistDetailDto;
import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistMapDto;
import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistRequestDto;
import com.ll.clearpath.domain.tourlist.tourlist.dto.TourlistResponseDto;
import com.ll.clearpath.domain.tourlist.tourlist.entity.Keyword;
import com.ll.clearpath.domain.tourlist.tourlist.entity.KeywordList;
import com.ll.clearpath.domain.tourlist.tourlist.entity.Tourlist;
import com.ll.clearpath.domain.tourlist.tourlist.repository.KeywordListRepository;
import com.ll.clearpath.domain.tourlist.tourlist.repository.KeywordRepository;
import com.ll.clearpath.domain.tourlist.tourlist.repository.TourlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TourlistService {
    private final TourlistRepository tourlistRepository;
    private final KeywordRepository keywordRepository;
    private final KeywordListRepository keywordListRepository;

    private static final double JEJU_CITY_HALL_LATITUDE = 33.499621;
    private static final double JEJU_CITY_HALL_LONGITUDE = 126.531188;

    @Transactional
    public void save(TourlistRequestDto tourlistRequestDto) {
        WebClient webClient = WebClient.builder().baseUrl("https://api.visitjeju.net/vsjApi/contents").build();

        int currentPage = 1;
        int totalPages = 1;

        do {
            try {
                String apiUrl = "/searchList?apiKey={apiKey}&locale={locale}&category={category}&page={page}";
                TourlistResponseDto tourlistResponseDto = webClient.get().uri(apiUrl, tourlistRequestDto.getApiKey(), tourlistRequestDto.getLocale(),
                        tourlistRequestDto.getCategory(), currentPage).retrieve().bodyToMono(TourlistResponseDto.class).block();

                if (tourlistResponseDto == null) {
                    throw new IllegalStateException("API 응답이 null입니다.");
                }

                if ("SUCCESS".equals(tourlistResponseDto.getResultMessage()) && "200".equals(String.valueOf(tourlistResponseDto.getResult()))) {
                    if (tourlistResponseDto.getItems() == null || tourlistResponseDto.getItems().isEmpty()) {
                        throw new IllegalStateException("API 응답에 항목이 없습니다.");
                    }

                    currentPage = tourlistResponseDto.getCurrentPage();
                    totalPages = tourlistResponseDto.getPageCount();
                    List<Tourlist> tourlists = tourlistResponseDto.getItems().stream()
                            .map(this::convertToEntity)
                            .collect(Collectors.toList());

                    try {
                        tourlistRepository.saveAll(tourlists);
                    } catch (Exception e) {
                        throw new IllegalStateException("데이터베이스 저장 중 오류 발생: ", e);
                    }

                    for (Tourlist tourlist : tourlists) {
                        String tags = tourlistResponseDto.getItems().stream()
                                .filter(item -> item.getTitle().equals(tourlist.getTitle()))
                                .map(TourlistDetailDto::getTag)
                                .findFirst()
                                .orElse("");
                        savetags(tourlist, tags);
                    }
                    currentPage++;
                } else {
                    throw new IllegalStateException("API 호출 실패 코드: " + tourlistResponseDto.getResult() + ", 메시지: " + tourlistResponseDto.getResultMessage());
                }
            } catch (Exception e) {
                throw new IllegalStateException("페이지 처리 중 오류 발생: ", e);
            }
        } while (currentPage <= totalPages);
    }

    @Transactional
    public void savetags(Tourlist tourlist, String tags){
        if (tags == null || tags.trim().isEmpty()) {
            return;
        }

        List<String> tagList = List.of(tags.split(",")).stream()
                .map(String::trim)
                .collect(Collectors.toList());
        for (String tagContent : tagList) {
            Keyword keyword = keywordRepository.findByContent(tagContent)
                    .orElseGet(() -> keywordRepository.save(Keyword.builder().content(tagContent).build()));
            KeywordList keywordList = KeywordList.builder()
                    .tourlist(tourlist)
                    .keyword(keyword)
                    .build();
            keywordListRepository.save(keywordList);

        }
    }

    public Tourlist convertToEntity(TourlistDetailDto tourlistDetailDto){
        return Tourlist.builder()
                .title(tourlistDetailDto.getTitle())
                .address(tourlistDetailDto.getAddress())
                .introduction(tourlistDetailDto.getIntroduction())
                .latitude(tourlistDetailDto.getLatitude())
                .longitude(tourlistDetailDto.getLongitude())
                .imgpath(tourlistDetailDto.getImgpath())
                .build();
    }

    public TourlistMapDto convertToDto(Tourlist tourlist) {
        String tags = tourlist.getKeywordLists().stream()
                .map(keywordList -> keywordList.getKeyword().getContent())
                .collect(Collectors.joining(", "));

        double distance = calculateDistance(JEJU_CITY_HALL_LATITUDE, JEJU_CITY_HALL_LONGITUDE, tourlist.getLatitude(), tourlist.getLongitude());

        return new TourlistMapDto(
                tourlist.getTitle(),
                tourlist.getLatitude(),
                tourlist.getLongitude(),
                distance,
                tourlist.getAddress(),
                tags,
                tourlist.getCurrentTemperature(),
                tourlist.getWeatherCondition()
        );
    }

    public List<TourlistMapDto> getAllTourlistForMap() {
        List<Tourlist> tourlists = tourlistRepository.findAll();
        return  tourlists.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers

        // 소수점 이하 첫 번째 자리까지 반올림
        distance = Math.round(distance * 10) / 10.0;

        return distance;
    }

    public List<TourlistMapDto> getTourlistByDistance(double radius) {
        List<Tourlist> tourlists = tourlistRepository.findAll();

        return tourlists.stream()
                .map(this::convertToDto)
                .filter(dto -> radius <= 0 || dto.getDistance() <= radius)
                .sorted(Comparator.comparingDouble(TourlistMapDto::getDistance))
                .collect(Collectors.toList());
    }

    public List<TourlistMapDto> searchTours(String category, String search, double radius, String weather) {
        List<Tourlist> tourlists = tourlistRepository.searchTours(category, search);

        String[] defaultWeatherConditions = {"맑음", "구름많음", "흐림", "흐리고 비", "눈"};
        List<String> weatherConditions = weather == null || weather.isEmpty()
                ? Arrays.asList(defaultWeatherConditions)
                : Arrays.asList(weather.split(","));

        return tourlists.stream()
                .map(this::convertToDto)
                .filter(dto -> radius <= 0 || dto.getDistance() <= radius)
                .filter(dto -> weatherConditions.contains(dto.getWeatherCondition()))
                .sorted(Comparator.comparingDouble(TourlistMapDto::getDistance))
                .collect(Collectors.toList());
    }
}
