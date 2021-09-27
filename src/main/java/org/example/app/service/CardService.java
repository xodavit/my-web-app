package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.exception.CardNotFoundException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.repository.CardRepository;
import org.example.app.repository.UserRepository;
import org.example.framework.security.Roles;

import java.util.List;

@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public List<Card> getAllByOwnerId(long ownerId) {
        return cardRepository.getAllByOwnerId(ownerId);
    }

    public Card getCardById(User ourOwner, long cardId) {
        final User cardOwner = userRepository.findCardOwnerByCardId(cardId).orElseThrow(UserNotFoundException::new);
        final var foundedUser = userRepository.findByTokenWithRole(ourOwner.getUsername());
        if (cardOwner.getId() == ourOwner.getId()
                || (foundedUser.isPresent()
                && !foundedUser.get().getRole().isEmpty()
                && foundedUser.get().getRole().equals(Roles.ROLE_ADMIN))) {
            return cardRepository.getCardById(cardId).orElseThrow(CardNotFoundException::new);
        } else {
            throw new UnsupportedOperationException("User not owner or don't have admin role!");
        }
    }

    //TODO ...
    public void getCardByNumber() {
    }

    public Card transferMoneyToAnotherYourselfCard(User currentUser, String fromCardNumber, String toCardNumber, long money) {
        final User cardUser = userRepository.findCardOwnerByCardNumber(fromCardNumber).orElseThrow(UserNotFoundException::new);

        if (cardUser.getId() == currentUser.getId()) {
            final var from = cardRepository.getCardByNumber(fromCardNumber).orElseThrow(CardNotFoundException::new);
            final var to = cardRepository.getCardByNumber(toCardNumber).orElseThrow(CardNotFoundException::new);
            return cardRepository.transferMoneyToAnotherYourselfCard(cardUser, from, to, money);

        } else {
            throw new UnsupportedOperationException("User not owner or don't have admin role");
        }
    }

    //TODO ...
    public void blockById() {
    }
}
