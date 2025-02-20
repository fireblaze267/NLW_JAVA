package br.com.nlw.events.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionRakingItem;
import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadotrNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repo.EventRepo;
import br.com.nlw.events.repo.SubscriptionRepo;
import br.com.nlw.events.repo.UserRepo;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {

        // recuperar o evento pelo nome
        Event evt = evtRepo.findByPrettyName(eventName);
        if (evt == null) { // caso evento não foi encontrado
            throw new EventNotFoundException("Evento " + eventName + " não existe");
        }

        User userRec = userRepo.findByEmail(user.getEmail());

        if (userRec == null) { // caso usuario não exista
            userRec = userRepo.save(user);
        }

        User indicador = null;

        if (userId != null) {
            indicador = userRepo.findById(userId).orElse(null);
            if (indicador == null) {
                throw new UserIndicadotrNotFoundException("usuario " + userId + " indicador não existe");
            }
        }

        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicador);

        Subscription tmpSub = subRepo.findByEventAndSubscriber(evt, userRec);
        if (tmpSub != null) { // caso usuario já esteja cadastrado em um evento
            throw new SubscriptionConflictException("Já existe inscrição para o usuário " + userRec.getName());
        }

        Subscription res = subRepo.save(subs);
        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/" + res.getEvent().getPrettyName() + "/" + res.getSubscriber().getId());
    }

    public List<SubscriptionRakingItem> getCompleteRanking(String prettyName) {
        Event evt = evtRepo.findByPrettyName(prettyName);
        if (evt == null) {
            throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe");
        }
        return subRepo.generateRanking(evt.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyname, Integer userId) {
        List<SubscriptionRakingItem> ranking = getCompleteRanking(prettyname);

        SubscriptionRakingItem item = ranking.stream().filter(i -> i.userId().equals(userId)).findFirst().orElse(null);
        if (item == null) {
            throw new UserIndicadotrNotFoundException("Não há inscrições com indicação do usuario " + userId);
        }

        Integer posicao = IntStream.range(0, ranking.size()).filter(pos -> ranking.get(pos).userId().equals(userId)).findFirst().getAsInt();

        return new SubscriptionRankingByUser(item, posicao+1);
    }

}
