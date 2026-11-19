package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mastercash.mastercash.domain.MovimentacaoDomain
import com.mastercash.mastercash.ui.viewmodel.MovimentacaoViewModel
import com.mastercash.mastercash.utils.FormatUtils
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(navController: androidx.compose.navigation.NavHostController, modifier: Modifier = Modifier) {
    val viewModel: MovimentacaoViewModel = hiltViewModel()

    val movimentacoes by viewModel.movimentacoes
    val totalEntradas by viewModel.totalEntradas
    val totalSaidas by viewModel.totalSaidas
    val saldo by viewModel.saldo
    val categoriaMaiorGasto by viewModel.categoriaMaiorGasto
    val toastMessage by viewModel.toastMessage

    val scope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        if (toastMessage.isNotEmpty()) {
            scope.launch {
                // Simple toast replacement using Snackbar will be shown via Scaffold
            }
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("form") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar movimentação",
                    tint = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "MasterCash", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Dashboard Cards
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                items(
                    listOf(
                        CardItem("total_entradas", FormatUtils.formatCurrency(totalEntradas ?: 0.0), Color(0xFF22C55E)),
                        CardItem("total_saidas", FormatUtils.formatCurrency(totalSaidas ?: 0.0), Color(0xFFEF4444)),
                        CardItem("saldo_atual", FormatUtils.formatCurrency(saldo ?: 0.0), if ((saldo ?: 0.0) >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)),
                        CardItem("maior_gasto", categoriaMaiorGasto ?: "-", Color(0xFF2563EB))
                    )
                ) { item ->
                    DashboardCard(
                        title = stringResource(id = com.mastercash.mastercash.R.string.total_entradas),
                        value = item.value,
                        color = item.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtros
            var filtroDescricao by remember { mutableStateOf("") }
            var filtroCategoria by remember { mutableStateOf("") }

            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filtroDescricao,
                    onValueChange = { filtroDescricao = it },
                    label = { Text("Pesquisar por descrição") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                DropdownMenu(
                    expanded = filtroCategoria.isNotEmpty(),
                    onDismissRequest = { filtroCategoria = "" }
                ) {
                    // Simplified: categorias fixas
                    val categoriasFixas = listOf("Alimentação", "Transporte", "Moradia", "Lazer", "Outros")
                    categoriasFixas.forEach { cat ->
                        DropdownMenuItem(
                            onClick = { filtroCategoria = cat }
                        ) {
                            Text(text = cat)
                        }
                    }
                }

                TextButton(
                    onClick = { filtroCategoria = "" }
                ) {
                    Text(text = "Limpar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Movimentações
            Text(
                text = "Movimentações Recentes",
                style = MaterialTheme.typography.titleMedium
            )

            val listaFiltrada = if (movimentacoes != null) {
                movimentacoes.filter { m ->
                    (filtroDescricao.isEmpty() || m.descricao.lowercase().contains(filtroDescricao.lowercase())) &&
                        (filtroCategoria.isEmpty() || m.categoria == filtroCategoria)
                }
            } else {
                emptyList()
            }

            if (listaFiltrada.isEmpty()) {
                Text(
                    text = stringResource(id = com.mastercash.mastercash.R.string.sem_movimentacoes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listaFiltrada) { m ->
                        MovimentacaoItem(
                            movimentacao = m,
                            onEdit = { navController.navigate("edit/${m.id}") },
                            onDelete = { viewModel.deleteMovimentacao(m.id) }
                        )
                    }
                }
            }
        }
    }
}

private data class CardItem(val title: String, val value: String, val color: Color)

@Composable
private fun DashboardCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.width(IntrinsicSize.Max),
        elevation = 2.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = color,
                fontWeight = androidx.compose.ui.text.FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MovimentacaoItem(
    movimentacao: MovimentacaoDomain,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val tipoCor = if (movimentacao.tipo == "entrada") Color(0xFF22C55E) else Color(0xFFEF4444)

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = 1.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movimentacao.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.FontWeight.Medium
                )
                Text(
                    text = "${movimentacao.data}  •  ${movimentacao.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column {
                Text(
                    text = FormatUtils.formatCurrency(movimentacao.valor),
                    style = MaterialTheme.typography.bodyMedium,
                    color = tipoCor,
                    fontWeight = androidx.compose.ui.text.FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}
