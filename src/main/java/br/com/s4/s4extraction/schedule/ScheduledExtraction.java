package br.com.s4.s4extraction.schedule;

import br.com.s4.s4extraction.entity.AccessLogs;
import br.com.s4.s4extraction.entity.CtlExtraction;
import br.com.s4.s4extraction.service.CtlExtractionService;
import br.com.s4.s4extraction.service.EventLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ScheduledExtraction {

    private static final Logger log = LoggerFactory.getLogger(ScheduledExtraction.class);

    private final CtlExtractionService ctlExtractionService;
    private final EventLoaderService eventLoaderService;

    public ScheduledExtraction(CtlExtractionService ctlExtractionService,
                               EventLoaderService eventLoaderService) {
        this.ctlExtractionService = ctlExtractionService;
        this.eventLoaderService = eventLoaderService;
    }

    // Runs every 30 minutes at 0 and 30 of each hour
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        List<String> spotList = Arrays.asList("1");
        for (String spotStr : spotList) {
            try {
                Long spotId = Long.valueOf(spotStr);
                List<CtlExtraction> rows = ctlExtractionService.findBySpotId(spotId);
                if (rows == null || rows.isEmpty()) {
                    log.info("[ScheduledExtraction] No extraction row for spotId={}, calling loadAllEvents", spotId);
                    // default limit aligned with controller default
                    List<AccessLogs> result = eventLoaderService.loadAllEvents(1000);

                    result.forEach(item -> {

                    });



                } else {
                    CtlExtraction row = rows.get(0);
                    Long lastPosition = row.getLastPosition();
                    int offset;
                    try {
                        offset = Math.toIntExact(lastPosition);
                    } catch (ArithmeticException ex) {
                        log.warn("[ScheduledExtraction] lastPosition={} exceeds int range, capping to Integer.MAX_VALUE for spotId={}", lastPosition, spotId);
                        offset = Integer.MAX_VALUE;
                    }
                    log.info("[ScheduledExtraction] Found extraction row for spotId={}, lastPosition={}, calling loadEventsSince(offset={})", spotId, lastPosition, offset);
                    String result = eventLoaderService.loadEventsSince(offset, 1000);

                }
            } catch (Exception e) {
                log.error("[ScheduledExtraction] Error processing spot {}: {}", spotStr, e.getMessage(), e);
            }
        }
    }
}
