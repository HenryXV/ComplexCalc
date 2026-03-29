package com.example.complexcalc;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private EditText visor;

    private String currentOperator;
    private boolean isNewCalculation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visor = findViewById(R.id.visor);
        visor.setInputType(InputType.TYPE_NULL);
    }

    public void btnAction(View v) {
        String tag = v.getTag().toString();

        switch (tag) {
            case "+":
            case "-":
            case "x":
            case "/":
                appendOperatorToVisor(tag);
                break;
            case "C":
                visor.getText().clear();
                currentOperator = null;
                break;
            case "<<":
                backspace();
                break;
            case "=":
                calculateResult();
                break;
            case ".":
                appendDecimalToVisor();
                break;
            default:
                appendNumberToVisor(tag);
                break;
        }
    }

    private void appendNumberToVisor(String v) {
        String currentText = visor.getText().toString();

        if (isNewCalculation) {
            visor.setText("" + v);
            isNewCalculation = false;
            return;
        }

        visor.setText(currentText + v);
    }

    private void appendOperatorToVisor(String v) {
        isNewCalculation = false;

        String currentText = visor.getText().toString();

        if (currentOperator != null && currentText.substring(currentText.length() - 2, currentText.length() - 1).equals(v)) {
            return;
        }

        if (currentOperator == null || currentOperator.equals(v)) {
            visor.setText(currentText + " " + v + " ");
            currentOperator = v;
        }
    }

    private void appendDecimalToVisor() {
        String currentText = visor.getText().toString();

        if (currentText.isEmpty() || isNewCalculation) {
            return;
        }

        char lastChar = currentText.charAt(currentText.length() - 1);
        if (!Character.isDigit(lastChar)) {
            return;
        }

        String[] parts = currentText.split(" ");
        if (parts[parts.length - 1].contains(".")) {
            return;
        }

        visor.setText(currentText + ".");
    }

    private void backspace() {
        String currentText = visor.getText().toString();

        if (currentText.isEmpty() || currentText.equals("ERROR") || isNewCalculation) {
            visor.getText().clear();
            currentOperator = null;
            return;
        }

        if (currentText.endsWith(" ")) {
            visor.setText(currentText.substring(0, currentText.length() - 3));

            if (!visor.getText().toString().contains(currentOperator)) {
                currentOperator = null;
            }
        } else {
            visor.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void calculateResult() {
        if (currentOperator == null) {
            return;
        }

        DoubleBinaryOperator operation = null;

        switch (currentOperator) {
            case "+":
                operation = Double::sum;
                break;
            case "-":
                operation = (a, b) -> a - b;
                break;
            case "x":
                operation = (a, b) -> a * b;
                break;
            case "/":
                operation = (a, b) -> a / b;
        }

        if (operation == null) {
            return;
        }

        String currentText = visor.getText().toString();

        String[] parts = currentText.split(" ");

        OptionalDouble result = Arrays.stream(parts)
                .filter(p -> !Objects.equals(p, currentOperator))
                .filter(p -> !p.trim().isEmpty())
                .mapToDouble(Double::parseDouble)
                .reduce(operation);

        if (result.isPresent()) {
            double valueNumber = result.getAsDouble();

            currentOperator = null;
            isNewCalculation = true;

            if (Double.isInfinite(valueNumber) || Double.isNaN(valueNumber)) {
                visor.setText("ERROR");
                return;
            }
            visor.setText("" + valueNumber);
        }
    }
}