package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mastercash.mastercash.domain.MovimentacaoDomain
import com.mastercash.mastercash.ui.viewmodel.MovimentacaoViewModel
import com.mastercash.mastercash.utils.FormatUtils
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraficosScreen(navController: androidx.navigation.NavHostController, modifier: Modifier = Modifier) {
    val viewModel: MovimentacaoViewModel = hiltViewModel()

    val movimentacoes by viewModel.movimentacoes.observeAsState(emptyList())
    val totalEntradas by viewModel.totalEntradas.observeAsState(0.0)
    val totalSaidas by viewModel.totalSaidas.observeAsState(0.0)
    val saldo by viewModel.saldo.observeAsState(0.0)

    var periodoGrafico by remember { mutableStateOf("mensal") }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Gráficos", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumoCard("Entradas", FormatUtils.formatCurrency(totalEntradas ?: 0.0), Color(0xFF22C55E), modifier = Modifier.weight(1f))
                ResumoCard("Saídas", FormatUtils.formatCurrency(totalSaidas ?: 0.0), Color(0xFFEF4444), modifier = Modifier.weight(1f))
                ResumoCard("Saldo", FormatUtils.formatCurrency(saldo ?: 0.0), if ((saldo ?: 0.0) >= 0) Color(0xFF16A34A) else Color(0xFFDC2626), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Evolução de Entradas e Saídas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Período: ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = { periodoGrafico = "semanal" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (periodoGrafico == "semanal") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Semanal")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { periodoGrafico = "mensal" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (periodoGrafico == "mensal") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Mensal")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AndroidChartView(
                movimentacoes = movimentacoes,
                periodo = periodoGrafico,
                modifier = Modifier.fillMaxWidth().height(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Distribuição por Categoria",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AndroidPieChartView(
                    title = "Entradas",
                    movimentacoes = movimentacoes,
                    tipo = "entrada",
                    modifier = Modifier.weight(1f).height(220.dp)
                )
                AndroidPieChartView(
                    title = "Saídas",
                    movimentacoes = movimentacoes,
                    tipo = "saida",
                    modifier = Modifier.weight(1f).height(220.dp)
                )
            }
        }
    }
}

@Composable
private fun ResumoCard(titulo: String, valor: String, cor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                color = cor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AndroidChartView(
    movimentacoes: List<MovimentacaoDomain>,
    periodo: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = true
                setTouchEnabled(true)
                setDragEnabled(true)
                setScaleEnabled(false)
                setPinchZoom(false)
            }
        },
        update = { chart ->
            chart.clear()
            val grupos = agruparPorPeriodo(movimentacoes, periodo)

            val entradasEntries = mutableListOf<BarEntry>()
            val saidasEntries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            grupos.forEachIndexed { index, grupo ->
                labels.add(grupo.label)
                entradasEntries.add(BarEntry(index.toFloat(), grupo.entrada.toFloat()))
                saidasEntries.add(BarEntry(index.toFloat(), grupo.saida.toFloat()))
            }

            val dataSetEntradas = BarDataSet(entradasEntries, "Entradas")
            dataSetEntradas.color = AndroidColor.parseColor("#22C55E")
            dataSetEntradas.valueTextColor = AndroidColor.parseColor("#22C55E")

            val dataSetSaidas = BarDataSet(saidasEntries, "Saídas")
            dataSetSaidas.color = AndroidColor.parseColor("#EF4444")
            dataSetSaidas.valueTextColor = AndroidColor.parseColor("#EF4444")

            val barData = BarData(dataSetEntradas, dataSetSaidas)
            barData.barWidth = 0.35f
            chart.data = barData

            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index >= 0 && index < labels.size) labels[index] else ""
                }
            }
            chart.xAxis.granularity = 1f
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            val leftAxis = chart.axisLeft
            leftAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return FormatUtils.formatCurrency(value.toDouble())
                }
            }
            leftAxis.axisMinimum = 0f

            chart.axisRight.isEnabled = false
            chart.invalidate()
        }
    )
}

@Composable
fun AndroidPieChartView(
    title: String,
    movimentacoes: List<MovimentacaoDomain>,
    tipo: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = if (tipo == "entrada") Color(0xFF22C55E) else Color(0xFFEF4444),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            factory = { context ->
                PieChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    description.isEnabled = false
                    legend.isEnabled = true
                    setUsePercentValues(true)
                    setDrawEntryLabels(true)
                    setHoleRadius(40f)
                    setTransparentCircleRadius(40f)
                }
            },
            update = { chart ->
                chart.clear()
                val valoresPorCategoria = mutableMapOf<String, Double>()
                movimentacoes.forEach { m ->
                    if (m.tipo == tipo) {
                        valoresPorCategoria[m.categoria] = (valoresPorCategoria[m.categoria] ?: 0.0) + m.valor
                    }
                }

                val entries = mutableListOf<PieEntry>()
                val colors = mutableListOf<Int>()
                val cores = listOf(
                    AndroidColor.parseColor("#3B82F6"),
                    AndroidColor.parseColor("#22C55E"),
                    AndroidColor.parseColor("#F59E0B"),
                    AndroidColor.parseColor("#8B5CF6"),
                    AndroidColor.parseColor("#EC4899"),
                    AndroidColor.parseColor("#14B8A6"),
                    AndroidColor.parseColor("#F97316"),
                    AndroidColor.parseColor("#64748B")
                )

                var index = 0
                valoresPorCategoria.forEach { (cat, valor) ->
                    entries.add(PieEntry(valor.toFloat(), cat))
                    colors.add(cores[index % cores.size])
                    index++
                }

                val dataSet = PieDataSet(entries, title)
                dataSet.colors = colors
                dataSet.valueTextSize = 10f
                dataSet.valueTextColor = AndroidColor.BLACK

                val pieData = PieData(dataSet)
                chart.data = pieData
                chart.invalidate()
            }
        )
    }
}

private data class GrupoPeriodo(val label: String, val entrada: Double, val saida: Double)

private fun agruparPorPeriodo(movimentacoes: List<MovimentacaoDomain>, periodo: String): List<GrupoPeriodo> {
    val grupos = mutableMapOf<String, GrupoPeriodo>()
    val ordenado = mutableListOf<String>()

    movimentacoes.forEach { m ->
        val key: String
        val label: String

        if (periodo == "semanal") {
            val partes = m.data.split("-")
            val dia = partes.getOrNull(2)?.toInt() ?: 1
            val mes = partes.getOrNull(1)?.toInt() ?: 1
            val ano = partes.getOrNull(0)?.toInt() ?: 2024
            key = "$ano-${String.format("%02d", mes)}"
            label = "${String.format("%02d", dia)}/${String.format("%02d", mes)}"
        } else {
            val partes = m.data.split("-")
            val ano = partes.getOrNull(0) ?: "2024"
            val mes = partes.getOrNull(1) ?: "01"
            key = "$ano-$mes"
            label = "$mes/$ano"
        }

        if (!grupos.containsKey(key)) {
            grupos[key] = GrupoPeriodo(label, 0.0, 0.0)
            ordenado.add(key)
        }
        val grupoAtual = grupos[key]!!
        val novoGrupo = GrupoPeriodo(
            grupoAtual.label,
            if (m.tipo == "entrada") grupoAtual.entrada + m.valor else grupoAtual.entrada,
            if (m.tipo == "saida") grupoAtual.saida + m.valor else grupoAtual.saida
        )
        grupos[key] = novoGrupo
    }

    return ordenado.map { grupos[it]!! }
}
