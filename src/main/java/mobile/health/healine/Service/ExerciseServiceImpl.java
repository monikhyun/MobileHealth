package mobile.health.healine.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.health.healine.Entity.*;
import mobile.health.healine.Entity.dto.AddedExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseDto;
import mobile.health.healine.Entity.dto.ExerciseRecordDto;
import mobile.health.healine.Repository.ExerciseRecordRepository;
import mobile.health.healine.Repository.ExerciseRepository;
import mobile.health.healine.Repository.MemberFavoriteRepository;
import mobile.health.healine.Repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService{
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberFavoriteRepository memberFavoriteRepository;
    private final MemberRepository memberRepository;

    // 추가된 운동 목록 조회
    @Override
    public List<AddedExerciseDto> findAddedExercise(String userId, LocalDate date) {
        Member member = memberRepository.findByUserId(userId);
        List<ExerciseRecord> addExercises = exerciseRecordRepository.findByMemberAndDate(member, date);
        return addExercises.stream()
                .map(v -> AddedExerciseDto.builder()
                        .bodyPart(v.getBodyPart())
                        .exercise_name(v.getExerciseName())
                        .done(false)
                        .build())
                .collect(Collectors.toList());
    }

    // 해당 운동 데이터 불러오기
    @Override
    public Exercise ExerciseData(String exerciseName) {
        return exerciseRepository.findByExerciseName(exerciseName);
    }

    // 기록할 운동 추가
    @Override
    public void addExercise(String userId, String exerciseName, LocalDate date) {
        Member member = memberRepository.findByUserId(userId);

        Exercise exercise = exerciseRepository.findByExerciseName(exerciseName);

        boolean alreadyExists = exerciseRecordRepository
                .existsByMemberAndExerciseNameAndDate(member, exerciseName, date);

        if (alreadyExists) {
            throw new IllegalStateException("이미 같은 운동이 등록되어 있습니다: "
                    + userId + " / " + exerciseName + " / " + date);
        }

        exerciseRecordRepository.save(ExerciseRecord.builder()
                        .member(member)
                        .bodyPart(exercise.getCategory())
                        .setCount(1)
                        .weight(new BigDecimal(1))
                        .count(0)
                        .exerciseName(exerciseName)
                        .done(false)
                        .date(date)
                .build());
    }
    // 기록할 운동 제거
    @Override
    @Transactional
    public void removeExercise(String userId, String exerciseName, LocalDate date) {
        Member member = memberRepository.findByUserId(userId);
        if (member == null) {
            throw new EntityNotFoundException("해당 유저를 찾을 수 없습니다: " + userId);
        }

        // 해당 유저·날짜·운동 이름에 매핑된 모든 ExerciseRecord 엔티티를 삭제
        List<ExerciseRecord> recordsToDelete =
                exerciseRecordRepository.findByMemberAndExerciseNameAndDate(member, exerciseName, date);
        if (recordsToDelete.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("삭제할 운동 기록이 없습니다: %s / %s / %s", userId, exerciseName, date));
        }

        exerciseRecordRepository.deleteAll(recordsToDelete);
    }

    // 운동 기록 저장
    @Override
    public void saveExercise(String userId, String exerciseName, ExerciseRecordDto exerciseRecordDto) {
            Member member = memberRepository.findByUserId(userId);

        int setNumber = exerciseRecordDto.getSetCount();

        Optional<ExerciseRecord> existing = exerciseRecordRepository.findByMemberAndExerciseNameAndDateAndSetCount(
                member, exerciseName, exerciseRecordDto.getDate(), setNumber
        );
        if (existing.isPresent()) {
            ExerciseRecord exerciseRecord = existing.get();
            exerciseRecord.setWeight(exerciseRecordDto.getWeight());
            exerciseRecord.setCount(exerciseRecordDto.getCount());
            exerciseRecord.setDone(exerciseRecordDto.getDone());
            exerciseRecordRepository.save(exerciseRecord);
        }else {
            // ── 신규 생성 ──
            Exercise exercise = exerciseRepository.findByExerciseName(exerciseName);
            ExerciseRecord newRec = ExerciseRecord.builder()
                    .member(member)
                    .bodyPart(exercise.getCategory())
                    .exerciseName(exerciseName)
                    .date(exerciseRecordDto.getDate())
                    .setCount(setNumber)
                    .weight(exerciseRecordDto.getWeight())
                    .count(exerciseRecordDto.getCount())
                    .done(exerciseRecordDto.getDone())
                    .build();
            exerciseRecordRepository.save(newRec);
        }
    }

    // 운동 기록 저장된 정보 조회
    @Override
    public List<ExerciseRecordDto> findRecord(String userId, String exerciseName,LocalDate date) {
        Member member = memberRepository.findByUserId(userId);

        List<ExerciseRecord> exerciseRecord = exerciseRecordRepository.findByMemberAndExerciseNameAndDate(member, exerciseName, date);

        return exerciseRecord.stream()
                .map(dto -> ExerciseRecordDto.builder()
                        .setCount(dto.getSetCount())
                        .count(dto.getCount())
                        .weight(dto.getWeight())
                        .done(dto.getDone())
                        .date(dto.getDate())
                        .build())
                .collect(Collectors.toList());
    }

    // 운동 기록 삭제
    @Override
    public void deleteRecord(String userId, String exerciseName,Integer setCount, LocalDate date) {
        Member member = memberRepository.findByUserId(userId);

        Optional<ExerciseRecord> exerciseRecord = exerciseRecordRepository.findByMemberAndExerciseNameAndDateAndSetCount
                (member, exerciseName, date, setCount);

        if (exerciseRecord.isPresent()) {
            exerciseRecordRepository.delete(exerciseRecord.get());
        }

        List<ExerciseRecord> laterSets = exerciseRecordRepository
                .findByMemberAndExerciseNameAndDateAndSetCountGreaterThan(
                        member, exerciseName, date, setCount);

        for (ExerciseRecord rec : laterSets) {
            rec.setSetCount(rec.getSetCount() - 1);
            exerciseRecordRepository.save(rec);
        }
    }

    // 전체 운동 목록
    @Override
    public List<ExerciseDto> findAllExercises() {
        return exerciseRepository.findAll().stream().map(v -> ExerciseDto.builder()
                .exercise_name(v.getExerciseName())
                .bodyPart(v.getCategory())
                .build())
                .collect(Collectors.toList());

    }

    // 운동 찜하기
    @Override
    public void likeExercise(String userId, String exerciseName) {
        if (memberFavoriteRepository.existsByMemberUserIdAndExerciseExerciseName(userId, exerciseName)) {
            return;
        }
        memberFavoriteRepository.save(MemberFavorite.builder()
                .member(memberRepository.findByUserId(userId))
                .exercise(exerciseRepository.findByExerciseName(exerciseName))
                .build());
  }
    // 운동 찜 하기 취소
    @Override
    public void unlikeExercise(String userId, String exerciseName) {
        memberFavoriteRepository.delete(MemberFavorite.builder()
                .member(memberRepository.findByUserId(userId))
                .exercise(exerciseRepository.findByExerciseName(exerciseName))
                .build());
    }

  // 찜한 운동 조회
    @Override
    public List<ExerciseDto> findFavoriteExercise(String userId) {
        List<MemberFavorite> memberFavorites = memberFavoriteRepository.findMemberFavoriteByMember
                (memberRepository.findByUserId(userId));
        return memberFavorites.stream()
                .map(dto -> ExerciseDto.builder()
                        .bodyPart(dto.getExercise().getCategory())
                        .exercise_name(dto.getExercise().getExerciseName())
                        .build())
                .collect(Collectors.toList());
    }

    // 운동 검색
    @Override
    public List<ExerciseDto> searchExercise(BodyPart bodyPart, String exerciseName) {
        List<Exercise> exercises;
        boolean hasName = exerciseName != null && !exerciseName.isBlank();
        boolean hasPart = bodyPart != null;

        if (hasName && hasPart) {
            // 이름 + 부위 검색 (정확 일치)
            exercises = exerciseRepository.findByCategoryAndExerciseNameContainingIgnoreCase(bodyPart,exerciseName);
        } else if (hasName) {
            // 이름만 검색 (포함 검색)
            exercises = exerciseRepository.findByExerciseNameContainingIgnoreCase(exerciseName);
        } else if (hasPart) {
            // 부위만 검색
            exercises = exerciseRepository.findByCategory(bodyPart);
        } else {
            // 둘 다 없으면 빈 리스트 반환하거나 전체 반환
            exercises = Collections.emptyList();
        }

        return exercises.stream()
                .map(dto -> ExerciseDto.builder()
                        .exercise_name(dto.getExerciseName())
                        .bodyPart(dto.getCategory())
                        .build())
                .collect(Collectors.toList());
    }
}
