package ch.chrigu.wotr.bot

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(3)
@Component
class CaptureSettlementStrategy : BotStrategy
