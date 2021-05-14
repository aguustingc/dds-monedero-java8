package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonederoTest {
  private Cuenta cuenta;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void MasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void seRegistraUnDeposito() {
    cuenta.poner(3000);
    Assertions.assertTrue(cuenta.getMovimientos().get(0).fueDepositado(LocalDate.now()));
  }

  @Test
  public void seRegistraUnaExtraccion() {
    cuenta.poner(500);
    cuenta.sacar(200);
    Assertions.assertTrue(cuenta.getMovimientos().get(1).fueExtraido(LocalDate.now()));
  }

  @Test
  public void seExtrae500PesosEnUnaFecha() {
    LocalDate fecha1 = LocalDate.of(2021,05,01);
    LocalDate fecha2 = LocalDate.of(2021,05,23);
    cuenta.agregarMovimiento(fecha1, 3000, true);
    cuenta.agregarMovimiento(fecha2, 200, false);
    cuenta.agregarMovimiento(fecha2, 300, false);

    Assertions.assertEquals(500, cuenta.getMontoExtraidoA(fecha2));
  }

  @Test
  public void TresDepositosYChequeoDeSaldo() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    Assertions.assertEquals(3856.0, cuenta.getSaldo());
  }

  @Test
  public void UnaExtraccionYChequeoDeSaldo() {
    cuenta.poner(3000);
    cuenta.sacar(700);
    Assertions.assertEquals(2300.0, cuenta.getSaldo());
  }

  @Test
  public void unDepositoEsUnDeposito() {
    cuenta.poner(3000);
    Assertions.assertTrue(cuenta.getMovimientos().get(0).isDeposito());
  }

}