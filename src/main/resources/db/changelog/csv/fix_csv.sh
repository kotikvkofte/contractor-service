#!/bin/bash

INPUT_FILE="org_form.csv"
OUTPUT_FILE="org_form-fix.csv"

# Очищаем выходной файл
> "$OUTPUT_FILE"

# Обрабатываем файл
first_line=true
while IFS= read -r line || [[ -n "$line" ]]; do
    # Удаляем DOS-переводы строк и лишние пробелы в конце
    line=$(echo "$line" | tr -d '\r' | sed 's/[[:space:]]*$//')

    if $first_line; then
        # Заголовок записываем без изменений
        echo "$line" >> "$OUTPUT_FILE"
        first_line=false
    else
        # Разделяем строку по первому вхождению ;
        IFS=';' read -r id name <<< "$line"
        # Записываем с кавычками вокруг name
        printf '%s;"%s"\n' "$id" "$name" >> "$OUTPUT_FILE"
    fi
done < "$INPUT_FILE"

echo "Файл успешно обработан. Результат сохранён в $OUTPUT_FILE"