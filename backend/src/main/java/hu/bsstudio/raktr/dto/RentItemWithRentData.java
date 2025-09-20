package hu.bsstudio.raktr.dto;

import hu.bsstudio.raktr.model.Rent;
import hu.bsstudio.raktr.model.RentItem;

public record RentItemWithRentData(RentItem rentItem, Rent rent) {
}
