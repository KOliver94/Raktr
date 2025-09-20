package hu.bsstudio.raktr.dto;

import hu.bsstudio.raktr.model.RentItem;
import java.util.Date;

public record RentItemWithOutDateAndRentId(RentItem rentItem, Date outDate, Long rentId) {
}
