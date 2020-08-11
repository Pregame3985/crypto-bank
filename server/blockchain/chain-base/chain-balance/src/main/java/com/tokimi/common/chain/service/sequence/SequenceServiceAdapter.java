package com.tokimi.common.chain.service.sequence;

import com.tokimi.chain.dao.SequenceDAO;
import com.tokimi.chain.entity.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;

import javax.annotation.Resource;
import javax.transaction.Transactional;

/**
 * @author william
 */
@Slf4j
public abstract class SequenceServiceAdapter implements SequenceService {

    @Resource
    private SequenceDAO sequenceDAO;

    @Override
    @Transactional
    public Long get(String address) {

        Sequence probe = new Sequence();
        probe.setAddress(address);

        return sequenceDAO.findOne(Example.of(probe))
                .orElseGet(() -> {
                    Sequence sequence = new Sequence();
                    sequence.setAddress(address);
                    sequence.setSequence(sync(address));
                    sequenceDAO.save(sequence);
                    return sequence;
                }).getSequence();
    }

    @Override
    @Transactional
    public void update(String address, Long seq) {

        Sequence probe = new Sequence();
        probe.setAddress(address);

        sequenceDAO.findOne(Example.of(probe)).ifPresentOrElse(sequence -> {
            sequence.setSequence(seq);
            sequenceDAO.save(sequence);
        }, () -> {
            Sequence sequence = new Sequence();
            sequence.setAddress(address);
            sequence.setSequence(seq);
            sequenceDAO.save(sequence);
        });
    }

    // @Override
    // protected BigDecimal threshold(Integer times) {

    //     Sequence probe = new Sequence();
    //     probe.setAddress(address);
    //     Optional<Sequence> any = sequenceDAO.findOne(Example.of(probe));

    //     Sequence sequence;

    //     if (any.isPresent()) {
    //         sequence = any.get();
    //     } else {
    //         sequence = new Sequence();
    //         sequence.setAddress(address);
    //     }

    //     GetTransactionCountResponse txCountRes = getTransactionCount(address);

    //     if (txCountRes.isSuccess()) {
    //         sequence.setSequence(BinaryUtils.hexToLong(txCountRes.getResult()));
    //     }

    //     log.info("address: {}, sequence: {}", address, sequence.getSequence());

    //     sequenceDAO.saveAndFlush(sequence);

    //     return sequence.getSequence().intValue();
    // }
}