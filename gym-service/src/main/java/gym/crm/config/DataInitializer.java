package gym.crm.config;

import gym.crm.model.Trainee;
import gym.crm.model.Trainer;
import gym.crm.model.TrainingType;
import gym.crm.model.User;
import gym.crm.repository.TraineeRepository;
import gym.crm.repository.TrainerRepository;
import gym.crm.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        TrainingType fitness = trainingTypeRepository.save(new TrainingType(null, "Fitness"));
        trainingTypeRepository.save(new TrainingType(null, "Yoga"));
        trainingTypeRepository.save(new TrainingType(null, "Zumba"));

        User trainerUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .build();

        Trainer trainer = Trainer.builder()
                .user(trainerUser)
                .specialization(fitness)
                .build();
        trainerRepository.save(trainer);

        User traineeUser = User.builder()
                .firstName("Alice")
                .lastName("Smith")
                .username("Alice.Smith")
                .password(passwordEncoder.encode("password123"))
                .isActive(true)
                .build();

        Trainee trainee = Trainee.builder()
                .user(traineeUser)
                .build();
        traineeRepository.save(trainee);

        System.out.println(">> Gym Service: Initial data loaded. Trainer: John.Doe / password123");
    }
}