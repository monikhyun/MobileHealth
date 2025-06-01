package mobile.health.healine.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.Diet;
import mobile.health.healine.Entity.dto.DietDto;
import mobile.health.healine.Repository.DietRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DietServiceImpl implements DietService{

    private final DietRepository dietRepository;
    private final MemberRepository memberRepository;

    @Override
    public void dietSave(DietDto dietDto) {
        Diet diet = Diet.builder()
                .member(memberRepository.findByUserId(dietDto.getUserId()))
                .name(dietDto.getName())
                .carbo(dietDto.getCarb())
                .protein(dietDto.getProtein())
                .fat(dietDto.getFat())
                .calories(dietDto.getCalories())
                .mealtime(dietDto.getMealtime())
                .date(dietDto.getDate())
                .build();
        dietRepository.save(diet);
    }

    @Override
    public List<DietDto> dietFind(String userId, LocalDate date) {
        Long MemberId = memberRepository.findByUserId(userId).getId();

        List<Diet> dietList = dietRepository.findByMemberIdAndDate(MemberId, date);



        return dietList.stream()
                .map(diet -> DietDto.builder()
                        .id(diet.getId())
                        .userId(diet.getMember().getUserId())
                        .name(diet.getName())
                        .mealtime(diet.getMealtime())
                        .carb(diet.getCarbo())
                        .protein(diet.getProtein())
                        .fat(diet.getFat())
                        .calories(diet.getCalories())
                        .date(diet.getDate())
                        .build())
                .toList();
    }

    @Override
    public DietDto dietFindOne(Long id) {
        Optional<Diet> diet = dietRepository.findById(id);
        if(diet.isPresent()){
            Diet findDiet = diet.get();
            return DietDto.fromEntity(findDiet);
        }
        else return null;

    }

    @Override
    public void dietUpdate(DietDto dietDto, Long id) {
        Diet diet = dietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식단 정보 없음"));
        diet.setName(dietDto.getName());
        diet.setCarbo(dietDto.getCarb());
        diet.setProtein(dietDto.getProtein());
        diet.setFat(dietDto.getFat());
        diet.setCalories(dietDto.getCalories());
        diet.setMealtime(dietDto.getMealtime());
    }
}
