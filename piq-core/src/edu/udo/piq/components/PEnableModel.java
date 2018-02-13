package edu.udo.piq.components;

public interface PEnableModel extends PSingleValueModel<Boolean> {
	
	public static final PEnableModel ALWAYS_ENABLED_MODEL = new PEnableModel() {
		@Override
		public void setValue(Object value) {
			if (!Boolean.TRUE.equals(value)) {
				throw new UnsupportedOperationException("ALWAYS_ENABLED_MODEL.setValue("+value+") is not possible");
			}
		}
		@Override
		public boolean isEnabled() {
			return true;
		}
		@Override
		public void addObs(PSingleValueModelObs<Boolean> obs) {
			// observers are not necessary since the value never changes
		}
		@Override
		public void removeObs(PSingleValueModelObs<Boolean> obs) {
			// observers are not necessary since the value never changes
		}
	};
	
	public static final PEnableModel ALWAYS_DISABLED_MODEL = new PEnableModel() {
		@Override
		public void setValue(Object value) {
			if (Boolean.TRUE.equals(value)) {
				throw new UnsupportedOperationException("ALWAYS_DISABLED_MODEL.setValue("+value+") is not possible");
			}
		}
		@Override
		public boolean isEnabled() {
			return false;
		}
		@Override
		public void addObs(PSingleValueModelObs<Boolean> obs) {
			// observers are not necessary since the value never changes
		}
		@Override
		public void removeObs(PSingleValueModelObs<Boolean> obs) {
			// observers are not necessary since the value never changes
		}
	};
	
	public static final boolean DEFAULT_ENABLED_VALUE = true;
	
	public default void toggleValue() {
		setValue(!getValue());
	}
	
	@Override
	public default Boolean getValue() {
		return Boolean.valueOf(isEnabled());
	}
	
	public boolean isEnabled();
	
}