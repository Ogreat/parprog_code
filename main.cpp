// Copyright 2019 Lalykin Oleg
#include <omp.h>
#include <iostream> 
#include <string>

class Task
{
public:
    //в программе сортируемая последовательность представлена в виде массива объединений Int32
    //целые числа представлены в системе счисления по основанию 256
    //доступ к отдельным байтам целого числа должен осуществляться через поле bytes объединения
    //байты, из которых состоит целое число, расположены в памяти в обратном порядке(little - endian)
    //сортировка по старшему байту, в котором находится знаковый бит, отличается от сортировки по другим разрядам числа
    union Int32
    {
        int x;
        unsigned char bytes[4];
    };
    union Int32 *list;//массив для параллельной версии
    union Int32 *listS;//массив для последовательной версии
    int *Step;//массив номеров элементов, откуда начинать
    int Size;//размер обрабатываемого массива
    int threads;//число потоков

    void initialization(char** argv)  //инициализация 
    {
        int i;
        Size = atoi(argv[1]);//размер массива берется из консоли

        list = new union Int32[Size];
        listS = new union Int32[Size];

        for (i = 0; i < Size; i++)
        {
            listS[i].x = list[i].x = rand() % 50 - 10;
        }

        threads = atoi(argv[2]);//число потоков берется из консоли
        Step = new int[threads + 1];

        for (i = 0; i < threads; i++)
        {
            Step[i] = i * Size / threads + (i * Size / threads) % 2;
        }
        Step[threads] = Size;
    }
    void Destructor()
    {
        delete[] Step;
        delete[] list;
        delete[] listS;
    }

    // Сортируем целые числа без знака по n-байту
    static void radix_sort_byte(union Int32* arr, int left, int right, int nByte)
    {

        union Int32 *p = new union Int32[right - left];
        //257 , т.к ещё есть знак "минус"
        int count[257] = { 0 }, i;

        for (i = left; i < right; i++)
            count[arr[i].bytes[nByte] + 1]++;

        for (i = 1; i < 256; i++)
            count[i] += count[i - 1];

        for (i = left; i < right; i++)
            p[count[arr[i].bytes[nByte]]++] = arr[i];

        for (i = left; i < right; i++)
            arr[i] = p[i - left];

    }

    // Сортировка целых чисел со знаком: переключаем знаковый бит, сортируем как беззнаковые, возвращаем знаковый бит
    static void radix_sort(union Int32* arr, int left, int right)
    {
        int i;
        unsigned *x = (unsigned*)arr;

        for (i = left; i < right; i++)
            x[i] ^= INT_MIN;

        // Сортируем числа от младшего байта к старшему
        for (i = 0; i < 4; i++)
            radix_sort_byte(arr, left, right, i);

        for (i = left; i < right; i++)
            x[i] ^= INT_MIN;
    }

    void  SortSubsequence(int sign, union Int32* mas, union Int32* tmp, int size1, int size2)
    {
        for (int i = sign; i < size1; i += 2)
            tmp[i] = mas[i];

        union Int32* mas2 = mas + size1;

        int a = sign;
        int b = sign;
        int i = sign;

        while ((a < size1) && (b < size2))
        {
            if (tmp[a].x <= mas2[b].x)
            {
                mas[i] = tmp[a];
                a += 2;
            }
            else
            {
                mas[i] = mas2[b];
                b += 2;
            }

            i += 2;
        }

        if (a == size1)
            for (int j = b; j < size2; j += 2, i += 2)
                mas[i] = mas2[j];
        else
            for (int j = a; j < size1; j += 2, i += 2)
                mas[i] = tmp[j];
    }

    void PairComparison(union Int32* mas, int size)
    {
        for (int i = 1; i < (size + 1) / 2; i++)
            if (mas[2 * i].x < mas[2 * i - 1].x)
            {
                int tmp = mas[2 * i - 1].x;
                mas[2 * i - 1] = mas[2 * i];
                mas[2 * i].x = tmp;
            }
    }


    void merge(union Int32* list, int size, int * step, int threads)

    {
        union Int32* tmp = new union Int32[size + 1];
        int i;
        int level = threads;
        int *restep = new int[threads + 1];
        for (int i = 0; i < threads; i++)
        {
            step[i] = i * size / threads + (i * size / threads) % 2;
        }
        step[threads] = size;
        while ((level != 1))
        {

#pragma omp parallel for private(i) 
            for (i = 0; i < level; i++)
            {
                int t = i % 2;
                SortSubsequence(t, list + step[i - t], tmp + step[i], step[i + (1 - t)] - step[i - t], step[i + 1 + (1 - t)] - step[i + (1 - t)]);
            }
#pragma omp parallel for private(i) 
            for (i = 0; i < level; i++)
            {
                if (i % 2 != 0)
                {
                    PairComparison(list + step[i - 1], step[i + 1] - step[i - 1]);
                }
            }
            level = level / 2;
            restep = new int[level];
            restep[0] = step[0];
            for (int i = 1; i < level; i++)
            {

                restep[i] = step[i * 2];

            }
            step = new int[level];
            for (int i = 0; i < level; i++)
            {
                step[i] = restep[i];
            }
            step[level] = size;
        }
    }
    void Check(union Int32* arr, int size) {
        bool f = true;
        for (int i = 0; i < size - 1; i++) {
            if (arr[i].x > arr[i + 1].x) {
                f = false;
            }
        }
        if (f) { std::cout << "\n\n CORRECT \n"; }
        else { std::cout << "\n\n FALSE \n"; }
    }

    void PrintTimeRes(double ts, double tp)
    {
        std::cout << "\n\n Time Serial Radix: " << ts;
        std::cout << "\n\n Time Parallel Radix with Batcher: " << tp;
        std::cout << "\n\n Jump: " << ts / tp;
    }

    void PrintList(std::string str, union Int32* arr, int size)
    {
        if (size < 33)
        {
            std::cout << "\n\n" << str << "\n";
            for (int i = 0; i < size; i++)
            {
                std::cout << arr[i].x << "|";
            }
        }
    }
    void PrintSteps(int * step, int threads)
    {
        std::cout << "\n\n" << " Steps:" << "\n";
        for (int j = 0; j < threads + 1; j++)
        {
            std::cout << "=>" << step[j];
        }

    }

    void RunTask(char **argv)
    {

        initialization(argv);

        PrintList(" Input: ", list, Size);
        PrintSteps(Step, threads);

        //Последовательная сортировка
        omp_set_num_threads(1);

        double start_timeS = omp_get_wtime();

        radix_sort(listS, 0, Size);

        double end_timeS = omp_get_wtime();

        double TimeS = end_timeS - start_timeS;

        //Параллельная сортировка
        omp_set_num_threads(threads);

        double start_timeP = omp_get_wtime();

        int i;
#pragma omp parallel for private(i) //каждый поток отсортирует свою часть
        for (i = 0; i < threads; i++)
        {
            radix_sort(list, Step[i], Step[i + 1]);
        }
        PrintList(" After radix: ", list, Size);
        if (threads > 1)
        {
            merge(list, Size, Step, threads);
        }

        double end_timeP = omp_get_wtime();

        double TimeP = end_timeP - start_timeP;

        PrintList(" After merge: ", list, Size);

        PrintTimeRes(TimeS, TimeP);
        Check(list, Size);
        Destructor();
    }

};
int main(int argc, char **argv)
{
    Task t;
    t.RunTask(argv);
    return 0;
}

